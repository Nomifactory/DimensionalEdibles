package jackyy.dimensionaledibles;

import jackyy.dimensionaledibles.block.*;
import jackyy.dimensionaledibles.command.*;
import jackyy.dimensionaledibles.proxy.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;

@Mod(modid = DimensionalEdibles.MODID,
    name = DimensionalEdibles.MODNAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = DimensionalEdibles.MCVERSION,
    dependencies = DimensionalEdibles.DEPENDS,
    useMetadata = true)
@SuppressWarnings("unused")
public class DimensionalEdibles {

    public static final String MCVERSION = "[1.12,1.13)";
    public static final String MODID = "dimensionaledibles";
    public static final String MODNAME = "Dimensional Edibles: Nomifactory Edition";
    public static final String DEPENDS = "after:waila;after:theoneprobe;after:jei@[4.12.0.0,);";
    public static final CreativeTabs TAB = new CreativeTabs(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.endCake);
        }
    };
    public static Logger logger = LogManager.getLogger(MODNAME);

    public static boolean isFTBLibsRunning = false;

    @SidedProxy(serverSide = "jackyy.dimensionaledibles.proxy.CommonProxy", clientSide = "jackyy.dimensionaledibles.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        BlockCustomCake.rebuildCache();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new DimensionalEdiblesCommand());
    }

}
