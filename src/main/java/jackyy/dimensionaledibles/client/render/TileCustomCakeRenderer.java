package jackyy.dimensionaledibles.client.render;

import jackyy.dimensionaledibles.block.tile.TileDimensionCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class TileCustomCakeRenderer extends TileEntitySpecialRenderer<TileDimensionCake> {

    public static final TileCustomCakeRenderer INSTANCE = new TileCustomCakeRenderer();

    @Override
    public void render(TileDimensionCake cake, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RendererUtils.RenderText(cake, x, y, z);
    }

}
