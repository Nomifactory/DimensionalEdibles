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

    private static final double SCALE = 0.0325;
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
        buffer.pos(faceWidth, 0, 0).tex(0.25, 0.125).endVertex();
        buffer.pos(0, 0, 0).tex(0.125, 0.125).endVertex();
    }

    @Override
    public void render(TileIslandCake cake, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (cake.isPersonalCake()) {
            RendererUtils.RenderText(cake.getCakeName(), x, y, z, 0x201FB7DC);
            RendererUtils.RenderText("Personal", x, y - 0.24F, z, 0x201FB7DC, 0.015F);
        } else {
            RendererUtils.RenderText(cake.getCakeName(), x, y, z, 0x20FFFFFF);
        }
    }

    @Override
    public boolean isGlobalRenderer(TileIslandCake cake) {
        return false;
    }

}
