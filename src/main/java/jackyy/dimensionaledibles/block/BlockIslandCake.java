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
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

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
                drops.add(
                        new ItemStack(
                                Item.REGISTRY.getObject(
                                        new ResourceLocation(ModConfig.tweaks.islandCake.personalLockingItem)
                                )
                        )
                );
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

        if (convertToPersonalCake(worldIn, pos, state, playerIn, hand)) {
            return true;
        }

        if (addFuelToCake(worldIn, pos, state, playerIn, hand)) {
            return true;
        }


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
        Item lockItem = Item.REGISTRY.getObject(new ResourceLocation(ModConfig.tweaks.islandCake.personalLockingItem));
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
        Island island;

        TileIslandCake tic = (TileIslandCake) worldIn.getTileEntity(pos);

        if (DimensionalEdibles.isFTBLibsRunning) {

            short teamUUID = FTBLibAPI.getTeamID(uuid);
            // check if player is in a team
            if (teamUUID != 0 && !tic.isPersonalCake()) {
                island = im.getIslandForTeam(teamUUID);
            } else { // fallback to create their own island (accessable with a personal island cake)
                island = im.getIslandForPlayer(uuid);
            }
        } else {
            island = im.getIslandForPlayer(uuid);
        }
        return island;
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
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileIslandCake();
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

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.getBlock() != this ? state : state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta % 6).withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = super.getMetaFromState(state);
        i = i | ((EnumFacing) state.getValue(FACING)).getHorizontalIndex() << 2;

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{BITES, FACING});
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite());

    }
}
