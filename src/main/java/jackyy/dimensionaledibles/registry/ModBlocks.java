package jackyy.dimensionaledibles.registry;

import jackyy.dimensionaledibles.DimensionalEdibles;
import jackyy.dimensionaledibles.block.*;
import jackyy.dimensionaledibles.block.tile.TileDimensionCake;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static BlockEndCake endCake = new BlockEndCake();
    public static BlockNetherCake netherCake = new BlockNetherCake();
    public static BlockOverworldCake overworldCake = new BlockOverworldCake();
    public static BlockCustomCake customCake = new BlockCustomCake();
    public static BlockIslandCake islandCake = new BlockIslandCake();

    public static void init(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(endCake, netherCake, overworldCake, islandCake, customCake);
        GameRegistry.registerTileEntity(TileDimensionCake.class, new ResourceLocation(DimensionalEdibles.MODID, "tile_dimension_cake"));
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        for (int i = 0; i <= 6; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(endCake), i, new ModelResourceLocation(endCake.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(netherCake), i, new ModelResourceLocation(netherCake.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(overworldCake), i, new ModelResourceLocation(overworldCake.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(islandCake), i, new ModelResourceLocation(islandCake.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(customCake), i, new ModelResourceLocation(customCake.getRegistryName(), "inventory"));
        }
    }

}
