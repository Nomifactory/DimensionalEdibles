package jackyy.dimensionaledibles.compat;

import jackyy.dimensionaledibles.block.BlockCakeBase;
import jackyy.dimensionaledibles.util.IWailaInfoProvider;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import java.util.List;

public class WailaCompat implements IWailaDataProvider {

    public static final WailaCompat INSTANCE = new WailaCompat();
    private static boolean registered;
    private static boolean loaded;

    public static void load(IWailaRegistrar registrar) {
        if (!registered) {
            throw new RuntimeException("Please register this handler using the provided method.");
        }
        if (!loaded) {
            registrar.registerBodyProvider(INSTANCE, BlockCakeBase.class);
            loaded = true;
        }
    }

    public static void register() {
        if (registered)
            return;
        registered = true;
        FMLInterModComms.sendMessage("waila", "register", "jackyy.dimensionaledibles.compat.WailaCompat.load");
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() instanceof IWailaInfoProvider) {
            return ((IWailaInfoProvider) accessor.getBlock()).getWailaBody(itemStack, currenttip, accessor, config);
        }
        return currenttip;
    }
}
