package jackyy.dimensionaledibles.registry;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.*;
import net.minecraft.util.math.*;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.client.event.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

@Config(modid = DimensionalEdibles.MODID, name = "DimensionalEdibles", category = DimensionalEdibles.MODID)
public class ModConfig {

    @Config.Comment("The Category for general features of the mod")
    public static General general = new General();
    @Config.Comment("The category for tweaking behaviors of mod features")
    public static Tweaks tweaks = new Tweaks();

    public interface CakeConfig {
        /**
         * Resource name of the fuel item for this cake.
         * @param dim target dimension (ignored except for CustomEdible)
         */
        String fuel(int dim);

        /** Whether this cake comes pre-fueled (true) or starts empty (false) */
        boolean preFueled();

        /** Whether this cake should consume fuel. */
        boolean consumesFuel();

        /**
         *  Whether to use custom coordinates
         * @param dim target dimension (ignored except for CustomEdible)
         */
        boolean useCustomCoordinates(int dim);

        /**
         * Custom Coordinates defined for this cake
         * @param dim target dimension (ignored except for CustomEdible)
         */
        CustomCoords customCoords(int dim);
    }

    public static class General {
        @Config.Comment("Set to true to enable End Cake.")
        public boolean endCake = true;
        @Config.Comment("Set to true to enable Nether Cake.")
        public boolean netherCake = true;
        @Config.Comment("Set to true to enable Overworld Cake.")
        public boolean overworldCake = true;
        @Config.Comment("Set to true to enable custom Cakes.")
        public boolean customCake = true;
        @Config.Comment("Set to true to enable Ender Apple.")
        public boolean enderApple = true;
        @Config.Comment("Set to true to enable Nether Apple.")
        public boolean netherApple = true;
        @Config.Comment("Set to true to enable Overworld Apple.")
        public boolean overworldApple = true;
        @Config.Comment("Set to true to enable Custom Apple.")
        public boolean customApple = true;

        @Config.Comment("Required operator level to invalidate stored cake spawning position")
        @Config.RangeInt(min = 1, max = 4)
        public int operatorInvalidationLevel = 1;
    }

    public static class CustomCoords {

        public CustomCoords() {}

        public CustomCoords(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Config.Comment("The X spawn coordinate")
        public double x = 0.0D;
        @Config.RangeDouble(min = 0.0D, max = 255.0D)
        @Config.Comment("The Y spawn coordinate")
        public double y = 64.0D;
        @Config.Comment("The Z spawn coordinate")
        public double z = 0.0D;

        public BlockPos toBlockPos() {
            return new BlockPos(x, y, z);
        }

        @Override
        public String toString() {
            return String.format("<%.2f,%.2f,%.2f>",x,y,z);
        }
    }

    public static class Tweaks {

        @Config.Comment("The category for dealing with the End Cake")
        public EndCake endCake = new EndCake();
        @Config.Comment("The category for dealing with the End Apple")
        public EnderApple enderApple = new EnderApple();
        @Config.Comment("The category for dealing with the Nether Cake")
        public NetherCake netherCake = new NetherCake();
        @Config.Comment("The category for dealing with the Nether Apple")
        public NetherApple netherApple = new NetherApple();
        @Config.Comment("The category dealing with the Overworld Cake")
        public OverworldCake overworldCake = new OverworldCake();
        @Config.Comment("The category for dealing with the Overworld Apple")
        public OverworldApple overworldApple = new OverworldApple();
        @Config.Comment("The category for dealing with the Island Cake")
        public IslandCake islandCake = new IslandCake();
        @Config.Comment("The category for defining and modifying a Custom Cake")
        public CustomEdible customEdible = new CustomEdible();
        @Config.Comment("Set to true to disable the activation of vanilla End Portal.")
        public boolean disableVanillaEndPortal = false;

        public static class EndCake implements CakeConfig {
            @Config.Comment("Set the fuel used by End Cake (Don't change this unless you know what you're doing).")
            public String fuel = "minecraft:ender_eye";
            @Config.Comment("Set to true to make the End Cake pre-fueled upon placed.")
            public boolean preFueled = false;
            @Config.Comment("Set to true to make the End Cake consume fuel.")
            public boolean consumeFuel = true;
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();

            // boilerplate
            public String fuel(int dim) { return fuel; }
            public boolean preFueled() { return preFueled; }
            public boolean consumesFuel() { return consumeFuel; }
            public boolean useCustomCoordinates(int dim) { return useCustomCoords; }
            public CustomCoords customCoords(int dim) { return customCoords; }
        }

        public static class EnderApple {
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();
        }

        public static class NetherCake implements CakeConfig {
            @Config.Comment("Set the fuel used by Nether Cake (Don't change this unless you know what you're doing).")
            public String fuel = "minecraft:obsidian";
            @Config.Comment("Set to true to make the Nether Cake pre-fueled upon placed.")
            public boolean preFueled = false;
            @Config.Comment("Set to true to make the Nether Cake consume fuel.")
            public boolean consumeFuel = true;
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();

            // boilerplate
            public String fuel(int dim) { return fuel; }
            public boolean preFueled() { return preFueled; }
            public boolean consumesFuel() { return consumeFuel; }
            public boolean useCustomCoordinates(int dim) { return useCustomCoords; }
            public CustomCoords customCoords(int dim) { return customCoords; }
        }

        public static class NetherApple {
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();
        }

        public static class OverworldCake implements CakeConfig {
            @Config.Comment("Set the fuel used by Overworld Cake (Don't change this unless you know what you're doing).")
            public String fuel = "minecraft:sapling";
            @Config.Comment("Set to true to make the Overworld Cake pre-fueled upon placed.")
            public boolean preFueled = false;
            @Config.Comment("Set to true to make the Overworld Cake consume fuel.")
            public boolean consumeFuel = true;
            @Config.Comment({
                "Set to true to make the Overworld Cake teleport players to world spawn.",
                "Otherwise, it will use the cached position."
            })
            public boolean useWorldSpawn = true;
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();

            // boilerplate
            public String fuel(int dim) { return fuel; }
            public boolean preFueled() { return preFueled; }
            public boolean consumesFuel() { return consumeFuel; }
            public boolean useCustomCoordinates(int dim) { return useCustomCoords; }
            public CustomCoords customCoords(int dim) { return customCoords; }
        }

        public static class OverworldApple {
            @Config.Comment({
                    "Set to true to make the Overworld Apple teleport players to world spawn.",
                    "Otherwise, it will use the cached position."
            })
            public boolean useWorldSpawn = true;
            @Config.Comment("Set to true to use custom coordinates for the teleportation.")
            public boolean useCustomCoords = false;
            @Config.Comment("Define the custom spawn coordinates")
            public CustomCoords customCoords = new CustomCoords();
        }

        public static class IslandCake {
            @Config.Comment("The Y-level that the island cake will teleport to")
            @Config.RangeInt(min = 0, max = 256)
            public int yLevel = 0;

            @Config.Comment("The gap between two islands in regions")
            @Config.RangeInt(min = 2, max = 100)
            public int islandGapRegions = 2;
        }

        public static class CustomEdible {
            @Config.Comment({
                    "Set a list of dimensions to add cakes / apples for.",
                    "Format: <Dimension ID>, <Cake / Apple Name>",
                    "Example: 0, Overworld",
                    "Note: \"Cake\" is automatically appended onto the end of the name for cakes."
            })
            public String[] dimensions = new String[0];
            @Config.Comment({
                    "Set a list of custom coordinates used by Custom Cakes / Apples, this is optional.",
                    "Format: <Dimension ID>, <X>, <Y>, <Z>",
                    "Example: 0, 420, 123, -420"
            })
            public String[] customCoords = new String[0];

            @Config.Comment("Customization of Custom Cake features")
            public CustomCake customCake = new CustomCake();

            public static class CustomCake {
                @Config.Comment("Set to true to make all Custom Cakes pre-fueled upon placed.")
                public boolean preFueled = false;
                @Config.Comment("Set to true to make all Custom Cakes consume fuel.")
                public boolean consumeFuel = true;
                @Config.Comment({
                        "Set the fuel used by Custom Cakes.",
                        "Format: <Dimension ID>, <Fuel Registry Name>",
                        "Example: 0, minecraft:apple"
                })
                public String[] fuel = new String[0];
            }
        }

    }

    @Mod.EventBusSubscriber
    public static class ConfigHolder {
        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(DimensionalEdibles.MODID)) {
                ConfigManager.sync(DimensionalEdibles.MODID, Config.Type.INSTANCE);
                BlockCustomCake.rebuildCache();
            }
        }
    }

}
