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

public class BlockEndCake extends BlockCakeBase implements ITileEntityProvider {

    public BlockEndCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":end_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".end_cake");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn,
                                          int meta) {
        return new TileDimensionCake(cakeDimension(), "End");
    }

    @Override
    protected ItemStack cakeFuel() {
        return new ItemStack(Objects.requireNonNull(
                Item.REGISTRY.getObject(new ResourceLocation(ModConfig.tweaks.endCake.fuel))));
    }

    @Override
    protected boolean useCustomCoordinates() {
        return ModConfig.tweaks.endCake.useCustomCoords;
    }

    @Override
    protected BlockPos customCoordinates() {
        return new BlockPos(ModConfig.tweaks.endCake.customCoords.x,
                            ModConfig.tweaks.endCake.customCoords.y,
                            ModConfig.tweaks.endCake.customCoords.z);
    }

    @Override
    protected int cakeDimension() {
        return 1;
    }

    @Override
    protected boolean consumesFuel() {
        return ModConfig.tweaks.endCake.consumeFuel;
    }

    @Override
    protected boolean isPreFueled() {
        return ModConfig.tweaks.endCake.preFueled;
    }

    @Override boolean registerItem() {
        return ModConfig.general.endCake;
    }
}
