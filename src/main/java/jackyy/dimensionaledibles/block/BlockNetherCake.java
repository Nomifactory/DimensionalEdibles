package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.Objects;

public class BlockNetherCake extends BlockCakeBase implements ITileEntityProvider {

    public BlockNetherCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":nether_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".nether_cake");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn,
                                          int meta) {
        return new TileDimensionCake(cakeDimension(), "Nether");
    }

    @Override
    protected ItemStack cakeFuel() {
        return new ItemStack(Objects.requireNonNull(
                Item.REGISTRY.getObject(new ResourceLocation(ModConfig.tweaks.netherCake.fuel))));
    }

    @Override
    protected int cakeDimension() {
        return -1;
    }

    @Override
    protected boolean consumesFuel() {
        return ModConfig.tweaks.netherCake.consumeFuel;
    }

    @Override
    protected boolean useCustomCoordinates() {
        return ModConfig.tweaks.netherCake.useCustomCoords;
    }

    @Override
    protected BlockPos customCoordinates() {
        return new BlockPos(ModConfig.tweaks.netherCake.customCoords.x,
                            ModConfig.tweaks.netherCake.customCoords.y,
                            ModConfig.tweaks.netherCake.customCoords.z);
    }

    @Override
    protected boolean isPreFueled() {
        return ModConfig.tweaks.netherCake.preFueled;
    }

    @Override boolean registerItem() {
        return ModConfig.general.netherCake;
    }
}
