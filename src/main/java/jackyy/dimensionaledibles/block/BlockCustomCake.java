package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.block.tile.*;
import jackyy.dimensionaledibles.registry.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.relauncher.*;
import org.apache.logging.log4j.*;

import java.util.Objects;

import static jackyy.dimensionaledibles.DimensionalEdibles.*;

public class BlockCustomCake extends BlockCakeBase implements ITileEntityProvider {

    private int customX = 0;
    private int customY = 0;
    private int customZ = 0;

    private ItemStack cakeFuel;
    private int cakeDimension = 0;

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
        this.cakeDimension = getDimension(world, pos);
        this.cakeFuel = determineCakeFuel();
        updateCustomCoordinates();

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    private int getDimension(World world,
                             BlockPos pos) {
        TileEntity ent = world.getTileEntity(pos);
        if (ent instanceof TileDimensionCake)
            return ((TileDimensionCake) ent).getDimensionID();
        throw new IllegalArgumentException("Specified position does not contain a Custom Cake");
    }

    private void updateCustomCoordinates() {
        for(String s : ModConfig.tweaks.customEdible.customCoords) {
            try {
                String[] parts = s.split(",");
                if (parts.length < 4) {
                    logger.log(Level.ERROR,
                               s + " is not a valid input line! Format needs to be: <dimID>, <x>, <y>, <z>");
                    continue;
                }
                if (Integer.parseInt(parts[0].trim()) == cakeDimension) {
                    customX = Integer.parseInt(parts[1].trim());
                    customY = Integer.parseInt(parts[2].trim());
                    customZ = Integer.parseInt(parts[3].trim());
                }
            } catch(NumberFormatException e) {
                logger.log(Level.ERROR,
                           s + " is not a valid line input! The dimension ID needs to be a number!");
            }
        }
    }

    private ItemStack determineCakeFuel() {
        String fuel = "minecraft:air";
        for(String s : ModConfig.tweaks.customEdible.customCake.fuel) {
            try {
                String[] parts = s.split(",");
                if (parts.length < 2) {
                    logger.log(Level.ERROR,
                               s + " is not a valid input line! Format needs to be: <dimID>, <cakeFuel>");
                    continue;
                }
                if (Integer.parseInt(parts[0].trim()) == cakeDimension) {
                    fuel = parts[1].trim();
                }
            } catch(NumberFormatException e) {
                logger.log(Level.ERROR,
                           s + " is not a valid line input! The dimension ID needs to be a number!");
            }
        }

        return new ItemStack(Objects.requireNonNull(
                Item.REGISTRY.getObject(new ResourceLocation(fuel))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab,
                             NonNullList<ItemStack> list) {
        if (registerItem()) {
            ItemStack stack;
            for(String s : ModConfig.tweaks.customEdible.dimensions) {
                try {
                    String[] parts = s.split(",");
                    if (parts.length < 2) {
                        logger.log(Level.ERROR,
                                   s + " is not a valid input line! Format needs to be: <dimID>, <cakeName>");
                        continue;
                    }
                    int dimension = Integer.parseInt(parts[0].trim());
                    if (DimensionManager.isDimensionRegistered(dimension)) {
                        stack = new ItemStack(this);
                        NBTTagCompound nbt = stack.getTagCompound();
                        if (nbt == null) {
                            nbt = new NBTTagCompound();
                            stack.setTagCompound(nbt);
                        }
                        nbt.setInteger("dimID", dimension);
                        nbt.setString("cakeName", parts[1].trim());
                        list.add(stack);
                    } else {
                        logger.log(Level.ERROR,
                                   parts[0] + " is not a valid dimension ID! (Needs to be a number)");
                    }
                } catch(NumberFormatException e) {
                    logger.log(Level.ERROR,
                               s + " is not a valid line input! The dimension ID needs to be a number!");
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world,
                                          int meta) {
        return new TileDimensionCake();
    }

    @Override
    protected ItemStack cakeFuel() {
        return cakeFuel;
    }

    @Override boolean registerItem() {
        return ModConfig.general.customCake;
    }

    @Override
    protected int cakeDimension() {
        return cakeDimension;
    }

    @Override
    protected boolean useCustomCoordinates() {
        return (customX != 0 && customY != 0 && customZ != 0);
    }

    @Override
    protected BlockPos customCoordinates() {
        return new BlockPos(customX, customY, customZ);
    }

    @Override
    protected boolean consumesFuel() {
        return ModConfig.tweaks.customEdible.customCake.consumeFuel;
    }

    @Override
    protected boolean isPreFueled() {
        return ModConfig.tweaks.customEdible.customCake.preFueled;
    }
}
