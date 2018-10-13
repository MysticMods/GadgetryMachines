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

public class TESRGrinder extends TileEntitySpecialRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESRGrinder() {
    super();
  }

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
    if (tile instanceof TileGrinder) {
      TileGrinder t = (TileGrinder) tile;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder b = tess.getBuffer();
      Vec4d sideUV = new Vec4d(0.5, 0, 0.125, 0.5);
      Vec4d faceUV = new Vec4d(0, 0, 0.5, 0.5);

      GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/grinder_parts.png"));
      GlStateManager.disableCull();

      float bladeAngle = 45.0f;
      if (t.progress[0] > 0) {
        bladeAngle += ((float) (t.progress[0] % 20) - partialTicks) * 18f;
      }

      GlStateManager.pushMatrix();
      GlStateManager.rotate(t.angle, 0, 1, 0);
      GlStateManager.translate(-0.3125, 0.1875, 0);
      GlStateManager.rotate(bladeAngle, 1.0f, 0, 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Primitives
          .addCubeToBuffer(b, -0.0625, -0.25, -0.25, 0.0625, 0.25, 0.25, new Vec4d[] { sideUV, sideUV, sideUV, sideUV, faceUV, faceUV }, 1f, 1f, 1f, 1f, true,
              true, true, true, true, true);
      tess.draw();
      GlStateManager.popMatrix();

      GlStateManager.pushMatrix();
      GlStateManager.rotate(t.angle, 0, 1, 0);
      GlStateManager.translate(0, 0.1875, 0);
      GlStateManager.rotate(bladeAngle + 120f, 1.0f, 0, 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Primitives
          .addCubeToBuffer(b, -0.0625, -0.25, -0.25, 0.0625, 0.25, 0.25, new Vec4d[] { sideUV, sideUV, sideUV, sideUV, faceUV, faceUV }, 1f, 1f, 1f, 1f, true,
              true, true, true, true, true);
      tess.draw();
      GlStateManager.popMatrix();

      GlStateManager.pushMatrix();
      GlStateManager.rotate(t.angle, 0, 1, 0);
      GlStateManager.translate(0.3125, 0.1875, 0);
      GlStateManager.rotate(bladeAngle + 240f, 1.0f, 0, 0);
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Primitives
          .addCubeToBuffer(b, -0.0625, -0.25, -0.25, 0.0625, 0.25, 0.25, new Vec4d[] { sideUV, sideUV, sideUV, sideUV, faceUV, faceUV }, 1f, 1f, 1f, 1f, true,
              true, true, true, true, true);
      tess.draw();
      GlStateManager.popMatrix();
      GlStateManager.translate(-(x + 0.5), -(y + 0.5), -(z + 0.5));

      GlStateManager.enableCull();
    }
  }
}
