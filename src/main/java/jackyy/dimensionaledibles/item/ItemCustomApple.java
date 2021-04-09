package jackyy.dimensionaledibles.item;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.registry.*;
import jackyy.dimensionaledibles.util.*;
import mcp.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.relauncher.*;

import javax.annotation.*;

import static jackyy.dimensionaledibles.DimensionalEdibles.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemCustomApple extends ItemFood {

    public ItemCustomApple() {
        super(4, 0.3F, false);
        setAlwaysEdible();
        setRegistryName(DimensionalEdibles.MODID + ":custom_apple");
        setTranslationKey(DimensionalEdibles.MODID + ".custom_apple");
        setCreativeTab(DimensionalEdibles.TAB);
    }

    @Override
    public void onFoodEaten(ItemStack stack,
                            World world,
                            EntityPlayer player) {
        int dimension;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("dimID")) {
            return;
        }
        dimension = nbt.getInteger("dimID");
        int customX = nbt.getInteger("x");
        int customY = nbt.getInteger("y");
        int customZ = nbt.getInteger("z");
        if (world.provider.getDimension() != dimension) {
            if (!world.isRemote) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                BlockPos coords;
                if (customX != 0 && customY != 0 && customZ != 0) {
                    coords = new BlockPos(customX, customY, customZ);
                } else {
                    coords = TeleporterHandler.getDimPos(playerMP, dimension, player.getPosition());
                }
                TeleporterHandler.updateDimPos(playerMP, world.provider.getDimension(), player.getPosition());
                TeleporterHandler.teleport(playerMP,
                                           dimension,
                                           coords.getX(),
                                           coords.getY(),
                                           coords.getZ(),
                                           playerMP.server.getPlayerList());
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 200, false, false));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab,
                            NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            if (ModConfig.general.customApple) {
                for(String s : ModConfig.tweaks.customEdible.dimensions) {
                    parseSubItemsString(s, list);
                }
            }
        }
    }

    private void parseSubItemsString(String s,
                                     NonNullList<ItemStack> list) {
        ItemStack stack;
        try {
            String[] parts = s.split(",");
            if (parts.length < 2) {
                logger.error("{} is not a valid input line! Format needs to be: <dimID>, <cakeName>", s);
                return;
            }
            int dimension = Integer.parseInt(parts[0]);
            if (DimensionManager.isDimensionRegistered(dimension)) {
                stack = new ItemStack(this);
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt == null) {
                    nbt = new NBTTagCompound();
                    stack.setTagCompound(nbt);
                }
                nbt.setInteger("dimID", dimension);
                nbt.setString("appleName", parts[1].trim());
                nbt.setInteger("x", 0);
                nbt.setInteger("y", 0);
                nbt.setInteger("z", 0);
                for(String c : ModConfig.tweaks.customEdible.customCoords) {
                    try {
                        String[] coords = c.split(",");
                        if (coords.length < 4) {
                            logger.error("{} is not a valid input line! Format needs to be: <dimID>, <x>, <y>, <z>", c);
                            continue;
                        }
                        if (Integer.parseInt(coords[0].trim()) == dimension) {
                            nbt.setInteger("x", Integer.parseInt(coords[1].trim()));
                            nbt.setInteger("y", Integer.parseInt(coords[2].trim()));
                            nbt.setInteger("z", Integer.parseInt(coords[3].trim()));
                        }
                    } catch(NumberFormatException e) {
                        logger.error("{} is not a valid line input! The dimension ID needs to be a number!", c, e);
                    }
                }
                list.add(stack);
            } else {
                logger.error("{} is not a valid dimension ID! (Needs to be a number)", parts[0]);
            }
        } catch(NumberFormatException e) {
            logger.error("{} is not a valid line input! The dimension ID needs to be a number!", s, e);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("appleName")) {
            return "Custom Apple";
        }
        return nbt.getString("appleName") + " Apple";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

}
