package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import javax.annotation.*;

public class BlockNetherCake extends BlockCakeBase implements ITileEntityProvider {

    public BlockNetherCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":nether_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".nether_cake");
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn,
                                          int meta) {
        return new TileDimensionCake(cakeDimension(), "Nether");
    }

    @Override
    protected ModConfig.CakeConfig config() { return ModConfig.tweaks.netherCake; }

    @Override
    protected int cakeDimension() { return -1; }

    @Override
    public boolean registerItem() { return ModConfig.general.netherCake; }
}
