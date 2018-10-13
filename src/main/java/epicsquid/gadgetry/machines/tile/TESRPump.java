package epicsquid.gadgetry.machines.tile;

import org.lwjgl.opengl.GL11;

import epicsquid.gadgetry.core.util.Primitives;
import epicsquid.gadgetry.core.util.Vec4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TESRPump extends TileEntitySpecialRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESRPump() {
    super();
  }

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
    if (tile instanceof TilePump) {
      TilePump t = (TilePump) tile;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder b = tess.getBuffer();

      GlStateManager.translate(x + 0.5, y + 0.5625, z + 0.5);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/pump_model.png"));
      GlStateManager.disableCull();

      float phase = -90.0f;
      if (t.progress[0] > 0) {
        phase += ((float) (t.progress[0])) * 9f;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(0, -(0.125 + 0.125 * Math.sin(Math.toRadians(phase))), 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Vec4d sideUV = new Vec4d(0, 0.25, 0.375, 0.125);
      Vec4d topUV = new Vec4d(0, 0.5, 0.375, 0.375);
      Primitives
          .addCubeToBuffer(b, -0.375, 0, -0.375, 0.375, 0.25, 0.375, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true,
              false, false, true, true);
      sideUV = new Vec4d(0, 0, 0.5, 3.0 / 32.0);
      topUV = new Vec4d(0.5, 0, 0.5, 0.5);
      Primitives
          .addCubeToBuffer(b, -0.5, 0.25, -0.5, 0.5, 0.4375, 0.5, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true,
              true, true, true, true);
      tess.draw();
      GlStateManager.popMatrix();

      GlStateManager.translate(-(x + 0.5), -(y + 0.5625), -(z + 0.5));

      GlStateManager.enableCull();
    }
  }
}
