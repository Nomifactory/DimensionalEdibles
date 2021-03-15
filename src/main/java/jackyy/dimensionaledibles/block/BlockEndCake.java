package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

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
    public ModConfig.CakeConfig config() {
        return ModConfig.tweaks.endCake;
    }

    @Override
    protected int cakeDimension() { return 1; }

    @Override
    public boolean registerItem() { return ModConfig.general.endCake; }
}
