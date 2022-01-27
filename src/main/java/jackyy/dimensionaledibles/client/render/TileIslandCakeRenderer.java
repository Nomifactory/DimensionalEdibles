package jackyy.dimensionaledibles.client.render;

import jackyy.dimensionaledibles.block.tile.TileIslandCake;
import jackyy.dimensionaledibles.registry.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import org.lwjgl.opengl.GL11;

//TODO renders the personal effect of a personal island cake
@SideOnly(Side.CLIENT)
public class TileIslandCakeRenderer extends TileEntitySpecialRenderer<TileIslandCake> {

    @Override
    public void render(TileIslandCake cake, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        String dimName = cake.getCakeName();
        float scale = 0.02666667F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1, z + 0.5F);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        int width = fontrenderer.getStringWidth(dimName) / 2;
        vertexbuffer.pos(x - width - 1, y - 1, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x - width - 1, y + 8, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x + width + 1, y + 8, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x + width + 1, y - 1, z).color(0F, 0F, 0, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(dimName, -fontrenderer.getStringWidth(dimName) / 2, 0, 553648127);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontrenderer.drawString(dimName, -fontrenderer.getStringWidth(dimName) / 2, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

    }

}
