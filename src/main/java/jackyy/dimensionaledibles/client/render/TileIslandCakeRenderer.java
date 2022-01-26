package jackyy.dimensionaledibles.client.render;

import jackyy.dimensionaledibles.block.tile.TileIslandCake;
import jackyy.dimensionaledibles.registry.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// renders the personal effect of a personal island cake
@SideOnly(Side.CLIENT)
public class TileIslandCakeRenderer extends TileEntitySpecialRenderer<TileIslandCake> {

    @Override
    public void render(TileIslandCake cake, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!cake.isPersonalCake()) return;


        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();



        GlStateManager.popMatrix();
        GlStateManager.popAttrib();

    }



    @Override
    public boolean isGlobalRenderer(TileIslandCake cake) {
        return false;
    }

}
