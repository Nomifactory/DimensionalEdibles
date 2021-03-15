package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.Nonnull;

import static jackyy.dimensionaledibles.util.TeleporterHandler.getDimPos;

public class BlockOverworldCake extends BlockCakeBase implements ITileEntityProvider {

    public BlockOverworldCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":overworld_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".overworld_cake");
    }

    @Override
    protected BlockPos calculateCoordinates(EntityPlayerMP player) {
        if (ModConfig.tweaks.overworldCake.useWorldSpawn)
            return getWordSpawnPos(player);
        else
            return getDimPos(player, cakeDimension(), player.getPosition());
    }

    private BlockPos getWordSpawnPos(EntityPlayerMP player) {
        WorldServer overworld = player.server.getPlayerList().getServerInstance().getWorld(cakeDimension());
        return overworld.getTopSolidOrLiquidBlock(overworld.getSpawnPoint());
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn,
                                          int meta) {
        return new TileDimensionCake(cakeDimension(), "Overworld");
    }

    @Override
    protected ModConfig.CakeConfig config() { return ModConfig.tweaks.overworldCake; }

    @Override
    protected int cakeDimension() { return 0; }

    @Override
    public boolean registerItem() { return ModConfig.general.overworldCake; }
}
