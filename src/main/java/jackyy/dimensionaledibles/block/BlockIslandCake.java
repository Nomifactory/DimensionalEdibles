package jackyy.dimensionaledibles.block;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import jackyy.dimensionaledibles.DimensionalEdibles;
import jackyy.dimensionaledibles.islands.Island;
import jackyy.dimensionaledibles.islands.IslandManager;
import jackyy.dimensionaledibles.registry.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

import static jackyy.dimensionaledibles.util.TeleporterHandler.teleport;
import static jackyy.dimensionaledibles.util.TeleporterHandler.updateDimPos;

public class BlockIslandCake extends BlockCakeBase {

    public boolean isPersonalIslandCake = false;


    public BlockIslandCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":island_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".island_cake");
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

        if (addFuelToCake(worldIn, pos, state, playerIn, hand)) {
            return true;
        }

        Island island = getIsland(worldIn, playerIn);

        if (worldIn.provider.getDimension() != this.cakeDimension()) {
            if (!worldIn.isRemote)
                if (playerIn.capabilities.isCreativeMode || !config().consumesFuel())
                    teleportPlayer(worldIn, playerIn, island.getTeleportLocation());
                else {
                    if (consumeCake(worldIn, pos, playerIn)) {
                        teleportPlayer(worldIn, playerIn, island.getTeleportLocation());
                    }
                }

            // has to return true for both server and client
            return true;
        }

        return false;
    }

    private Island getIsland(World worldIn, EntityPlayer playerIn) {
        UUID uuid = playerIn.getUniqueID();
        IslandManager im = IslandManager.forWorld(worldIn);
        Island island;

        if (DimensionalEdibles.isFTBLibsRunning) {
            short teamUUID = FTBLibAPI.getTeamID(uuid);
            // check if player is in a team
            if (teamUUID != 0 && !this.isPersonalIslandCake) {
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
}
