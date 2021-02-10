package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import static jackyy.dimensionaledibles.util.TeleporterHandler.*;

public class BlockOverworldCake extends BlockCakeBase implements ITileEntityProvider {

    public BlockOverworldCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":overworld_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".overworld_cake");
    }

    @Override
    protected BlockPos calculateCoordinates(EntityPlayerMP player) {
        if (ModConfig.tweaks.overworldCake.useWorldSpawn) {
            WorldServer overworld = player.server.getPlayerList().getServerInstance().getWorld(cakeDimension());
            return overworld.getTopSolidOrLiquidBlock(overworld.getSpawnPoint());
        } else
            return getDimPos(player, cakeDimension(), player.getPosition());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn,
                                          int meta) {
        return new TileDimensionCake(cakeDimension(), "Overworld");
    }

    @Override
    protected String cakeFuel() {
        return ModConfig.tweaks.overworldCake.fuel;
    }

    @Override
    protected boolean useCustomCoordinates() {
        return ModConfig.tweaks.overworldCake.useCustomCoords;
    }

    @Override
    protected BlockPos customCoordinates() {
        return new BlockPos(ModConfig.tweaks.overworldCake.customCoords.x,
                            ModConfig.tweaks.overworldCake.customCoords.y,
                            ModConfig.tweaks.overworldCake.customCoords.z);
    }

    @Override
    protected int cakeDimension() {
        return 0;
    }

    @Override
    protected boolean consumesFuel() {
        return ModConfig.tweaks.overworldCake.consumeFuel;
    }

    @Override
    protected boolean isPreFueled() {
        return ModConfig.tweaks.overworldCake.preFueled;
    }

    @Override boolean registerItem() {
        return ModConfig.general.overworldCake;
    }
}
