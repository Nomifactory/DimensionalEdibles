package jackyy.dimensionaledibles.block;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import jackyy.dimensionaledibles.DimensionalEdibles;
import jackyy.dimensionaledibles.block.tile.TileIslandCake;
import jackyy.dimensionaledibles.islands.Island;
import jackyy.dimensionaledibles.islands.IslandManager;
import jackyy.dimensionaledibles.registry.ModConfig;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static jackyy.dimensionaledibles.util.TeleporterHandler.teleport;
import static jackyy.dimensionaledibles.util.TeleporterHandler.updateDimPos;

public class BlockIslandCake extends BlockCakeBase implements ITileEntityProvider {


    public BlockIslandCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":island_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".island_cake");
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileIslandCake tile = (TileIslandCake) world.getTileEntity(pos);
        if (tile != null) {

            if (tile.isPersonalCake()) {
                Item lockingItem = getItemFromResourceString(ModConfig.tweaks.islandCake.personalLockingItem);
                if (lockingItem == null) {
                    DimensionalEdibles.logger.warn("No locking item set, defaulting to minecraft:diamond");
                    lockingItem = Items.DIAMOND;
                }

                drops.add(new ItemStack(lockingItem));
            }
        }
        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public boolean onBlockActivated(World worldIn,
                                    BlockPos pos,
                                    IBlockState state,
                                    EntityPlayer playerIn,
                                    EnumHand hand,
                                    EnumFacing side,
                                    float hitX,
                                    float hitY,
                                    float hitZ) {
        if (!(worldIn.getTileEntity(pos) instanceof TileIslandCake))
            return false;

        if (convertToPersonalCake(worldIn, pos, state, playerIn, hand))
            return true;

        if (addFuelToCake(worldIn, pos, state, playerIn, hand))
            return true;


        if (worldIn.provider.getDimension() != this.cakeDimension()) {
            if (!worldIn.isRemote) {
                Island island = getIsland(worldIn, playerIn, pos);
                if (playerIn.capabilities.isCreativeMode || !config().consumesFuel())
                    teleportPlayer(worldIn, playerIn, island.getTeleportLocation());
                else {
                    if (consumeCake(worldIn, pos, playerIn)) {
                        teleportPlayer(worldIn, playerIn, island.getTeleportLocation());
                    }
                }
            }
            // has to return true for both server and client
            return true;
        }

        return false;
    }

    private boolean convertToPersonalCake(World worldIn,
                                          BlockPos pos,
                                          IBlockState state,
                                          EntityPlayer playerIn,
                                          EnumHand hand) {

        if (!ModConfig.tweaks.islandCake.allowPersonalIslands) return false;


        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof TileIslandCake)) return false;
        TileIslandCake tic = (TileIslandCake) te;

        ItemStack stack = playerIn.getHeldItem(hand);
        Item lockItem = getItemFromResourceString(ModConfig.tweaks.islandCake.personalLockingItem);

        if (lockItem == null) {
            DimensionalEdibles.logger.warn("Lock item not found, won't convert to personal cake");
            return false;
        }

        if (!stack.isEmpty() &&
                ItemStack.areItemsEqual(stack, new ItemStack(lockItem)) &&
                !tic.isPersonalCake()) {

            tic.setPersonalCake(true);
            tic.setOwner(playerIn.getUniqueID());
            if (!playerIn.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return true;
        }

        return false;
    }

    private Island getIsland(World worldIn, EntityPlayer playerIn, BlockPos pos) {
        UUID uuid = playerIn.getUniqueID();
        IslandManager im = IslandManager.forWorld(worldIn);
        Island island = null;
        //already done a check for this position
        TileIslandCake tic = (TileIslandCake) worldIn.getTileEntity(pos);
        
        if (!tic.isPersonalCake() && DimensionalEdibles.isFTBLibsRunning) {
            short teamUUID = FTBLibAPI.getTeamID(uuid);
            // check if player is in a team
            if (teamUUID != 0) {
                island = im.getIslandForTeam(teamUUID);
            }
        }

        return island != null ? island : im.getIslandForPlayer(uuid);
    }


    protected void teleportPlayer(World world, EntityPlayer player, BlockPos spawnLoc) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        updateDimPos(playerMP, world.provider.getDimension(), playerMP.getPosition());
        teleport(playerMP, cakeDimension(), spawnLoc, playerMP.server.getPlayerList());
    }

    @Override
    protected ModConfig.CakeConfig config() {
        return ModConfig.tweaks.islandCake;
    }

    @Override
    protected int cakeDimension() {
        return ModConfig.tweaks.islandCake.islandDimension;
    }

    @Override
    protected boolean registerItem() {
        return ModConfig.general.islandCake;
    }

    @Nonnull
    @Override
    protected ItemStack defaultFuel() {
        return new ItemStack(Blocks.COBBLESTONE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileIslandCake();
    }

    public Item getItemFromResourceString(String resource) {
        if (resource == null || resource.isEmpty()) {
            return null;
        }

        return Item.REGISTRY.getObject(new ResourceLocation(resource));
    }

    @Override
    public void addProbeInfo(ProbeMode mode,
                             IProbeInfo probeInfo,
                             EntityPlayer player,
                             World world,
                             IBlockState blockState,
                             IProbeHitData data) {

        TileIslandCake tic = (TileIslandCake) world.getTileEntity(data.getPos());

        if (tic != null) {
            if (tic.isPersonalCake()) {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .text(TextFormatting.BLUE + "Personal");
            }
        }

        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack,
                                     List<String> currentTip,
                                     IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {
        TileIslandCake tic = (TileIslandCake) accessor.getTileEntity();

        if (tic != null) {
            if (tic.isPersonalCake()) {
                currentTip.add(TextFormatting.BLUE + "Personal");
            }

        }

        return super.getWailaBody(itemStack, currentTip, accessor, config);
    }
}
