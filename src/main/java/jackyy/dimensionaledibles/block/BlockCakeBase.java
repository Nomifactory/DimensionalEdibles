package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.*;
import jackyy.dimensionaledibles.registry.*;
import jackyy.dimensionaledibles.util.*;
import mcjty.theoneprobe.api.*;
import mcp.mobius.waila.api.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.util.*;
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

import javax.annotation.*;
import java.util.*;

import static jackyy.dimensionaledibles.DimensionalEdibles.*;
import static jackyy.dimensionaledibles.item.ItemBlockCustomCake.*;
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

    /** Cake config holder object */
    abstract protected ModConfig.CakeConfig config();

    /** Target dimension for this cake. */
    abstract protected int cakeDimension();

    /** Whether this item should be registered. */
    abstract protected boolean registerItem();

    /** The default fuel for this cake type, in the event of a configuration parse error. */
    @Nonnull
    abstract protected ItemStack defaultFuel();

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
        if (addFuelToCake(worldIn, pos, state, playerIn, hand)) {
            return true;
        }

        if (worldIn.provider.getDimension() != this.cakeDimension()) {
            if (!worldIn.isRemote)
                if (playerIn.capabilities.isCreativeMode || !config().consumesFuel())
                    teleportPlayer(worldIn, playerIn);
                else {
                    if (consumeCake(worldIn, pos, playerIn)) {
                        teleportPlayer(worldIn, playerIn);
                    }
                }

            // client and server both need to report event was handled (Fixes #12)
            return true;
        }

        return false;
    }

    public boolean addFuelToCake(World worldIn, BlockPos cakePos, IBlockState state, EntityPlayer playerIn, EnumHand hand) {
        int meta = getMetaFromState(worldIn.getBlockState(cakePos)) - 1;
        int fuelUntilFull = getMetaFromState(state);

        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() &&
            ItemStack.areItemsEqual(stack, getFuelItemStack(this.cakeDimension())) &&
            fuelUntilFull != 0) {
            if (meta >= 0) {
                worldIn.setBlockState(cakePos, state.withProperty(BITES, meta), 2);
                if (!playerIn.capabilities.isCreativeMode)
                    stack.shrink(1);
            }
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
        if (!this.canBlockStay(worldIn, pos))
            worldIn.setBlockToAir(pos);
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

        ItemStack fuelStack = getFuelItemStack(this.cakeDimension());
        //Only the custom cake falls back to a default empty ItemStack, so if the ItemStack is empty, the cake is a
        //custom cake with a bad fuel entry
        String fuel = fuelStack.isEmpty() ? "{*tooltip.dimensionaledibles.custom_cake.bad_config*}" :
                String.format("{*%s.name*}", fuelStack.getTranslationKey());

        if (world.getBlockState(data.getPos()).getBlock() instanceof BlockCakeBase) {
            probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                     .item(new ItemStack(Items.CAKE))
                     .text(TextFormatting.GREEN + "Bites: ")
                     .progress(MAX_BITES - blockState.getValue(BITES), MAX_BITES);
            probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                     .item(fuelStack.isEmpty() ? new ItemStack(Blocks.BARRIER) : fuelStack)
                     .text(TextFormatting.GREEN + "Refill: " + fuel);
        }
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack,
                                     List<String> currentTip,
                                     IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {

        ItemStack fuelStack = getFuelItemStack(this.cakeDimension());
        String fuel = fuelStack.isEmpty() ? I18n.format("tooltip.dimensionaledibles.custom_cake.bad_config") :
                I18n.format(fuelStack.getTranslationKey() + ".name");

        if (accessor.getBlockState().getBlock() instanceof BlockCakeBase) {
            currentTip.add(TextFormatting.GRAY + "Bites: " +
                           (MAX_BITES - accessor.getBlockState().getValue(BITES)) + " / " + MAX_BITES);
            currentTip.add(TextFormatting.GRAY + "Refill: " + fuel);
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
        return getDefaultState().withProperty(BITES, config().preFueled() ? 0 : MAX_BITES);
    }

    /**
     * Teleport the player to this cake's dimension.
     */
    protected void teleportPlayer(World world,
                                  EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        BlockPos coords;
        if (config().useCustomCoordinates(this.cakeDimension()))
            coords = config().customCoords(this.cakeDimension()).toBlockPos();
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
    protected boolean consumeCake(World world,
                                  BlockPos pos,
                                  EntityPlayer player) {
        if (player.canEat(true)) {
            int l = world.getBlockState(pos).getValue(BITES);
            if (l < MAX_BITES) {
                player.getFoodStats().addStats(2, 0.1F);
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BITES, l + 1), 3);
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Cake Fuel as an ItemStack to maintain NBT data and Metadata.
     *
     * @return The Fuel as an ItemStack if the Config entry is well-formed,
     *         {@link #defaultFuel} otherwise.
     */
    private ItemStack getFuelItemStack(int dim) {
        String fuel = config().fuel(dim);
        if (fuel == null || fuel.equals("")) {
            logger.error("Could not parse fuel for cake (dimension \"{}\"). Falling back to default fuel.", dim);
            return defaultFuel();
        }
        Item configItem = Item.REGISTRY.getObject(new ResourceLocation(fuel));
        return configItem == null ? defaultFuel() : new ItemStack(configItem);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab,
                             NonNullList<ItemStack> list) {
        if (registerItem())
            list.add(new ItemStack(this));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ItemStack fuelStack = getFuelItemStack(getDimID(stack));
        if (fuelStack == ItemStack.EMPTY)
            tooltip.add(I18n.format("tooltip.dimensionaledibles.custom_cake.bad_config"));
        else
            // Why do I need to add ".name"? Thank you Lex.
            tooltip.add(I18n.format("tooltip.dimensionaledibles.cake",
                    I18n.format(fuelStack.getTranslationKey() + ".name")));
    }
}