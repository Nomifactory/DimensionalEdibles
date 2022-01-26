package jackyy.dimensionaledibles.block;

import com.feed_the_beast.ftblib.FTBLibCommon;
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.item.*;
import jackyy.dimensionaledibles.registry.*;
import jackyy.dimensionaledibles.util.*;
import mcjty.theoneprobe.api.*;
import mcp.mobius.waila.api.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import org.apache.logging.log4j.message.*;

import javax.annotation.*;

import java.util.*;

import static jackyy.dimensionaledibles.DimensionalEdibles.logger;
import static net.minecraftforge.common.DimensionManager.isDimensionRegistered;

public class BlockCustomCake extends BlockCakeBase implements ITileEntityProvider {

    /** Dimension of the last-clicked cake. */
    private int cakeDimension;

    private static class CustomCake {
        public ModConfig.CustomCoords customCoords = new ModConfig.CustomCoords(0,0,0);
        private String cakeFuel = null;
        private final int cakeDimension;

        public CustomCake(int dim) {
            this.cakeDimension = dim;
        }

        @Override
        public String toString() {
            return String.format("CustomCake[dim: %d, fuel: %s, coords: %s]",
                                 cakeDimension, cakeFuel, customCoords);
        }
    }

    private static Cache<Integer, CustomCake> cache = new Cache<>();

    public BlockCustomCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":custom_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".custom_cake");

    }

    @Override
    public boolean onBlockActivated(World world,
                                    BlockPos pos,
                                    IBlockState state,
                                    EntityPlayer player,
                                    EnumHand hand,
                                    EnumFacing side,
                                    float hitX,
                                    float hitY,
                                    float hitZ) {

        int dim = getDimension(world, pos);
        if (!isDimensionRegistered(dim)) {
            Message message = new FormattedMessage(
                "Requested dimension: \"{}\" does not exist. Please verify your configs.",
                dim);
            if (!world.isRemote)
                logger.error(message);
            else
                player.sendMessage(new TextComponentString(message.getFormattedMessage()));
            return true;
        }

        this.cakeDimension = dim;

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    private int getDimension(World world,
                             BlockPos pos) {
        TileEntity ent = world.getTileEntity(pos);
        if (ent instanceof TileDimensionCake)
            return ((TileDimensionCake) ent).getDimensionID();
        throw new IllegalArgumentException("Specified position does not contain a Custom Cake");
    }

    /**
     *  DO NOT CALL THIS METHOD OUTSIDE OF PREINIT AND CONFIG CHANGE EVENT HANDLERS.
     *  This should be private but Forge forced my hand.
     */
    public static void rebuildCache() {
        Cache<Integer,CustomCake> newCache = new Cache<>();

        NonNullList<ItemStack> subBlocks = NonNullList.create();
        new BlockCustomCake().getSubBlocks(CreativeTabs.BUILDING_BLOCKS, subBlocks);
        for(ItemStack stack : subBlocks) {
            int dimID = ItemBlockCustomCake.getDimID(stack);
            newCache.putIfAbsent(dimID, new CustomCake(dimID));
        }

        for(String s : ModConfig.tweaks.customEdible.customCoords) {
            try {
                String[] parts = s.split(",");
                if (parts.length < 4) {
                    logger.error("\"{}\" is not a valid input line! Format needs to be: <dimID>, <x>, <y>, <z>", s);
                    continue;
                }
                int dim = Integer.parseInt(parts[0].trim());
                if(!newCache.containsKey(dim)) {
                    logger.error("Unrecognized dimension: \"{}\"", dim);
                    return;
                }
                CustomCake cake = newCache.get(dim);

                ModConfig.CustomCoords cc = cake.customCoords;
                cc.x = Integer.parseInt(parts[1].trim());
                cc.y = Integer.parseInt(parts[2].trim());
                cc.z = Integer.parseInt(parts[3].trim());

            } catch(NumberFormatException e) {
                logger.error("\"{}\" is not a valid line input! The dimension ID needs to be a number!", s, e);
                return;
            }
        }

        for(String s : ModConfig.tweaks.customEdible.customCake.fuel) {
            try {
                String[] parts = s.split(",");
                if (parts.length < 2) {
                    logger.error("\"{}\" is not a valid input line! Format needs to be: <dimID>, <cakeFuel>", s);
                    return;
                }
                int dim = Integer.parseInt(parts[0].trim());
                CustomCake cake = newCache.get(dim);

                cake.cakeFuel = parts[1].trim();
            } catch(NumberFormatException e) {
                logger.error("\"{}\" is not a valid line input! The dimension ID needs to be a number!", s, e);
                return;
            }
        }

        cache = newCache;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab,
                             NonNullList<ItemStack> list) {
        if (registerItem()) {
            ItemStack stack;
            for(String s : ModConfig.tweaks.customEdible.dimensions) {
                try {
                    String[] parts = s.split(",");
                    if (parts.length < 2) {
                        logger.error("\"{}\" is not a valid input line! Format needs to be: <dimID>, <cakeName>", s);
                        continue;
                    }
                    int dimension = Integer.parseInt(parts[0].trim());

                    // Always register the requested cakes; JED dimensions may not be loaded yet
                    stack = new ItemStack(this);
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt == null) {
                        nbt = new NBTTagCompound();
                        stack.setTagCompound(nbt);
                    }
                    nbt.setInteger("dimID", dimension);
                    nbt.setString("cakeName", parts[1].trim());
                    list.add(stack);

                } catch(NumberFormatException e) {
                    logger.error("\"{}\" is not a valid line input! The dimension ID needs to be a number!", s, e);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world,
                                          int meta) {
        return new TileDimensionCake();
    }

    private final ModConfig.CakeConfig conf = new ModConfig.CakeConfig() {
        @Override
        public String fuel(int dim) {
            return cache.getPropertyIfPresentOrNull(dim, c -> c.cakeFuel);
        }

        @Override
        public boolean useCustomCoordinates(int dim) {
            return cache.getPropertyIfPresentOrElse(dim, c -> {
                ModConfig.CustomCoords cc = c.customCoords;
                return (cc.x != 0 || cc.y != 0 || cc.z != 0);
            }, () -> false);
        }

        @Override
        public ModConfig.CustomCoords customCoords(int dim) {
            return cache.getPropertyIfPresentOrElse(dim, c -> c.customCoords, () -> new CustomCake(dim).customCoords);
        }

        @Override
        public boolean consumesFuel() { return ModConfig.tweaks.customEdible.customCake.consumeFuel; }
        @Override
        public boolean preFueled() { return ModConfig.tweaks.customEdible.customCake.preFueled; }
    };

    @Override
    protected ModConfig.CakeConfig config() { return conf; }

    @Override
    public boolean registerItem() { return ModConfig.general.customCake; }

    @Override
    public int cakeDimension() { return cakeDimension; }

    @Override
    @Nonnull
    public ItemStack defaultFuel() { return ItemStack.EMPTY; }

    /*
        These overrides work around the bug causing log spam in tooltips when
        looking at a cake. We should still consider splitting up custom cakes
        into separate objects per dimension as hot-swapping state is brittle.
     */
    @Override
    public void addProbeInfo(ProbeMode mode,
                             IProbeInfo probeInfo,
                             EntityPlayer player,
                             World world,
                             IBlockState blockState,
                             IProbeHitData data) {
        this.cakeDimension = getDimension(world, data.getPos());
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack,
                                     List<String> currentTip,
                                     IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {
        this.cakeDimension = getDimension(accessor.getWorld(), accessor.getPosition());
        return super.getWailaBody(itemStack, currentTip, accessor, config);
    }
}
