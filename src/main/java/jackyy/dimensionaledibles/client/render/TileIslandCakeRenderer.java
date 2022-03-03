package jackyy.dimensionaledibles.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import jackyy.dimensionaledibles.block.tile.TileIslandCake;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


// renders the personal effect of a personal island cake
@SideOnly(Side.CLIENT)
public class TileIslandCakeRenderer extends TileEntitySpecialRenderer<TileIslandCake> {
    public static TileIslandCakeRenderer INSTANCE = new TileIslandCakeRenderer();

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
