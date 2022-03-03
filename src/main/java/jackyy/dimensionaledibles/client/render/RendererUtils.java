package jackyy.dimensionaledibles.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;


public class RendererUtils {

    public static void RenderText(String text, double x, double y, double z, int color, float scale ) {
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
        int width = fontrenderer.getStringWidth(text) / 2;
        vertexbuffer.pos(x - width - 1, y - 1, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x - width - 1, y + 8, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x + width + 1, y + 8, z).color(0F, 0F, 0, 0.25F).endVertex();
        vertexbuffer.pos(x + width + 1, y - 1, z).color(0F, 0F, 0, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, color);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void RenderText(String text, double x, double y, double z, int color) {
       RenderText(text, x, y, z, color, 0.02666667F);
    }

}
