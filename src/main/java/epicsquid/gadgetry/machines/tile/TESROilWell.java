package epicsquid.gadgetry.machines.tile;

import org.lwjgl.opengl.GL11;

import epicsquid.gadgetry.core.util.Primitives;
import epicsquid.gadgetry.core.util.Vec4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TESROilWell extends TileEntitySpecialRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESROilWell() {
    super();
  }

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
    if (tile instanceof TileOilWell) {
      int i = tile.getWorld().getCombinedLight(tile.getPos().up(2), 0);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
      TileOilWell t = (TileOilWell) tile;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder b = tess.getBuffer();

      GlStateManager.pushMatrix();
      GlStateManager.translate(x + 0.5, y + 2, z + 0.5);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/oil_well.png"));
      GlStateManager.disableCull();

      float phase = -90.0f;
      if (t.progress[0] > 0 && t.active) {
        phase += ((float) (t.progress[0]) - partialTicks) * 4.5f;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(0, -(0.125 + 0.125 * Math.sin(Math.toRadians(phase))), 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Vec4d sideUV = new Vec4d(0.5, 0, 0.375, 0.25);
      Vec4d topUV = new Vec4d(0.5, 0.5, 0.375, 0.375);
      Primitives
          .addCubeToBuffer(b, -0.375, 0, -0.375, 0.375, 0.5, 0.375, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true,
              true, false, true, true);
      tess.draw();
      GlStateManager.translate(0, -(0.125 + 0.125 * Math.sin(Math.toRadians(phase))), 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      sideUV = new Vec4d(0.5, 0.25, 0.25, 0.25);
      topUV = new Vec4d(0.5625, 0.5625, 0.25, 0.25);
      Primitives
          .addCubeToBuffer(b, -0.25, 0.5, -0.25, 0.25, 1.0, 0.25, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true,
              true, false, true, true);
      tess.draw();
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/oil_well_model.png"));
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      sideUV = new Vec4d(0.75, 0.5, 0.1875, 0.25);
      topUV = new Vec4d(0.5, 0.5, 0.1875, 0.1875);
      Primitives
          .addCubeToBuffer(b, -0.1875, 1.0, -0.1875, 0.1875, 1.5, 0.1875, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true,
              true, false, false, true, true);
      tess.draw();

      GlStateManager.rotate(phase, 0, 1, 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Vec4d xUV = new Vec4d(0.5, 0.25, 0.5, -0.25);
      Vec4d yUV = new Vec4d(0, 0.5, 0.25, 0.5);
      Vec4d zUV = new Vec4d(0, 0.25, 0.25, -0.25);
      Primitives
          .addCubeToBuffer(b, -0.25, 1.5, -0.5, 0.25, 2.0, 0.5, new Vec4d[] { zUV, zUV, yUV, yUV, xUV, xUV }, 1f, 1f, 1f, 1f, true, true, true, true, true,
              true);
      tess.draw();
      GlStateManager.popMatrix();

      GlStateManager.popMatrix();
      GlStateManager.enableCull();
    }
  }
}
