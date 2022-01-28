package jackyy.dimensionaledibles.client.render;

import jackyy.dimensionaledibles.block.tile.TileIslandCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// renders the personal effect of a personal island cake
@SideOnly(Side.CLIENT)
public class TileIslandCakeRenderer extends TileEntitySpecialRenderer<TileIslandCake> {
    public static TileIslandCakeRenderer INSTANCE = new TileIslandCakeRenderer();

    private final Map<UUID, ResourceLocation> resourceLocations = new HashMap<>();
    private final int faceX = 8, faceY = 8;
    private final int overlayX = 40, overlayY = 8;
    private final int faceWidth = 16, faceHeight = 16;

    private static final double SCALE = 0.0625;
    private static final double DOWNSCALE = 0.65;
    private static final double OFFSET = (1 - DOWNSCALE) / 2;

    private ResourceLocation getSkinForPlayer(UUID id) {
        if (!resourceLocations.containsKey(id)) {
            //TODO implement
        }

        return resourceLocations.getOrDefault(id, DefaultPlayerSkin.getDefaultSkin(id));
    }

    private void addFaceToBuffer(BufferBuilder buffer) {
        buffer.pos(0, faceHeight, 0).tex(0.125, 0.25).endVertex();
        buffer.pos(faceWidth, faceHeight, 0).tex(0.25, 0.25).endVertex();
        buffer.pos(faceWidth, 0, 0).tex(0.25,0.125).endVertex();
        buffer.pos(0, 0, 0).tex(0.125, 0.125).endVertex();
    }

    @Override
    public void render(TileIslandCake cake, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RendererUtils.RenderText(cake, x, y, z);
        if (!cake.isPersonalCake()) return;
        /*
        double SCALE = 0.0625;
        double DOWNSCALE = 0.65;
        double OFFSET = (1 - DOWNSCALE) / 2;
        */
        ResourceLocation resourceLocation = getSkinForPlayer(cake.owner);

        GlStateManager.pushMatrix();

        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);

        GlStateManager.translate(x + OFFSET, y + 0.501 + 0.5, z + OFFSET);
        GlStateManager.scale(SCALE * DOWNSCALE, SCALE * DOWNSCALE, SCALE * DOWNSCALE);
        GlStateManager.rotate(90, 1, 0, 0);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        addFaceToBuffer(buffer);

        GlStateManager.enableTexture2D();

        tessellator.draw();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileIslandCake cake) {
        return false;
    }

}
