package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.util.*;
import mcjty.theoneprobe.api.*;
import mcp.mobius.waila.api.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

import static jackyy.dimensionaledibles.util.TeleporterHandler.*;

/**
 * This is based on the vanilla cake class, but slightly modified and added
 * Waila / TOP support.
 */
public abstract class BlockCakeBase extends Block implements ITOPInfoProvider, IWailaInfoProvider {

    /** The number of bites remaining in a fully-fueled cake. */
    public static final int MAX_BITES = 6;

    /**
     * "Bites" property for tracking damage to the cake.
     * Zero bites is a fully-fueled cake. MAX_BITES is an empty cake.
     */
    public static final PropertyInteger BITES = PropertyInteger.create("bites", 0, MAX_BITES);

    /**
     * Bounding Box for cakes.
     * Each entry in the array corresponds to a specific value of the BITES property.
     */
    public static final AxisAlignedBB[] CAKE_AABB =
        new AxisAlignedBB[]{
            new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.3125D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.4375D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.5625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.6875D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D),
            new AxisAlignedBB(0.8125D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D)
        };

    public BlockCakeBase() {
        super(Material.CAKE);
        setDefaultState(this.blockState.getBaseState().withProperty(BITES, 0));
        setTickRandomly(true);
        setHardness(0.5F);
        setSoundType(SoundType.CLOTH);
        setCreativeTab(DimensionalEdibles.TAB);
    }

    /**
     * Whether this cake comes pre-fueled (true) or starts empty (false)
     */
    abstract protected boolean isPreFueled();

    /**
     * Whether this cake should consume fuel.
     */
    abstract protected boolean consumesFuel();

    /**
     * Whether to use custom coordinates
     */
    abstract protected boolean useCustomCoordinates();

    /**
     * Custom Coordinates defined for this cake
     */
    abstract protected BlockPos customCoordinates();

    /**
     * Target dimension for this cake.
     */
    abstract protected int cakeDimension();

    /**
     * Item of the fuel item for this cake.
     */
    // The efficiency of this method is best-case O(1), worst-case O(logN) due
    // to the underlying implementation of the Item Registry using a HashMap
    abstract protected ItemStack cakeFuel();

    /**
     * Whether this item should be registered.
     */
    abstract boolean registerItem();

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state,
                                        IBlockAccess source,
                                        BlockPos pos) {
        return CAKE_AABB[state.getValue(BITES)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state,
                                                World worldIn,
                                                BlockPos pos) {
        return state.getCollisionBoundingBox(worldIn, pos);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn,
                                    BlockPos pos,
                                    IBlockState state,
                                    EntityPlayer playerIn,
                                    EnumHand hand,
                                    EnumFacing side,
                                    float hitX,
                                    float hitY,
                                    float hitZ) {
        int meta = getMetaFromState(worldIn.getBlockState(pos)) - 1;
        int fuelUntilFull = getMetaFromState(state);

        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() &&
            ItemStack.areItemsEqual(stack, this.cakeFuel()) &&
            fuelUntilFull != 0) {
            if (meta >= 0) {
                worldIn.setBlockState(pos, state.withProperty(BITES, meta), 2);
                if (!playerIn.capabilities.isCreativeMode)
                    stack.shrink(1);
                return true;
            }
        } else if (worldIn.provider.getDimension() != this.cakeDimension())
            if (!worldIn.isRemote) {
                if (playerIn.capabilities.isCreativeMode || !this.consumesFuel())
                    teleportPlayer(worldIn, playerIn);
                else
                    consumeCake(worldIn, pos, playerIn);
                return true;
            }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn,
                                   BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state,
                                World worldIn,
                                BlockPos pos,
                                Block blockIn,
                                BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World worldIn,
                                 BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state,
                               Random rand,
                               int fortune) {
        return Items.AIR;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BITES, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BITES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BITES);
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState,
                                          World worldIn,
                                          BlockPos pos) {
        return (7 - blockState.getValue(BITES)) * 2;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode,
                             IProbeInfo probeInfo,
                             EntityPlayer player,
                             World world,
                             IBlockState blockState,
                             IProbeHitData data) {
        if (world.getBlockState(data.getPos()).getBlock() instanceof BlockCakeBase) {
            probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                     .item(new ItemStack(Items.CAKE))
                     .text(TextFormatting.GREEN + "Bites: ")
                     .progress(MAX_BITES - blockState.getValue(BITES), MAX_BITES);
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack,
                                     List<String> currentTip,
                                     IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {
        if (accessor.getBlockState().getBlock() instanceof BlockCakeBase) {
            currentTip.add(TextFormatting.GRAY + "Bites: " +
                           (MAX_BITES - accessor.getBlockState().getValue(BITES)) + " / " + MAX_BITES);
        }
        return currentTip;
    }

    /**
     * Configure the starting state of the Cake when placed.
     */
    @Override
    public IBlockState getStateForPlacement(World world,
                                            BlockPos pos,
                                            EnumFacing facing,
                                            float hitX,
                                            float hitY,
                                            float hitZ,
                                            int meta,
                                            EntityLivingBase placer,
                                            EnumHand hand) {
        return getDefaultState().withProperty(BITES, isPreFueled() ? 0 : MAX_BITES);
    }

    /**
     * Teleport the player to this cake's dimension.
     */
    protected void teleportPlayer(World world,
                                  EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        BlockPos coords;
        if (useCustomCoordinates())
            coords = customCoordinates();
        else
            coords = calculateCoordinates(playerMP);

        updateDimPos(playerMP, world.provider.getDimension(), playerMP.getPosition());
        teleport(playerMP, cakeDimension(), coords, playerMP.server.getPlayerList());
    }

    /**
     * Calculate the teleportation location in the other dimension.
     */
    protected BlockPos calculateCoordinates(EntityPlayerMP player) {
        return getDimPos(player, cakeDimension(), player.getPosition());
    }

    /**
     * The player eats the cake, then teleports.
     */
    protected void consumeCake(World world,
                               BlockPos pos,
                               EntityPlayer player) {
        if (player.canEat(true)) {
            int l = world.getBlockState(pos).getValue(BITES);
            if (l < MAX_BITES) {
                player.getFoodStats().addStats(2, 0.1F);
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BITES, l + 1), 3);
                teleportPlayer(world, player);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab,
                             NonNullList<ItemStack> list) {
        if (registerItem())
            list.add(new ItemStack(this));
    }
}