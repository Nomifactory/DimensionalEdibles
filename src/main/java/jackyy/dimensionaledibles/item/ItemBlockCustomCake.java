package jackyy.dimensionaledibles.item;

import jackyy.dimensionaledibles.block.tile.TileDimensionCake;
import jackyy.dimensionaledibles.registry.ModBlocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBlockCustomCake extends ItemBlock {

    public ItemBlockCustomCake() {
        super(ModBlocks.customCake);
        setRegistryName(ModBlocks.customCake.getRegistryName());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getCakeName(stack) + " Cake";
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState blockState) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, blockState);

        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            if (te instanceof TileDimensionCake) {
                ((TileDimensionCake) te).setDimensionID(getDimID(stack));
                ((TileDimensionCake) te).setCakeName(getCakeName(stack));
            }
        }

        return placed;
    }

    public String getCakeName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("cakeName")) {
            return "Custom";
        }
        return nbt.getString("cakeName");
    }

    public static int getDimID(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("dimID")) {
            return 0;
        }
        return nbt.getInteger("dimID");
    }

}
