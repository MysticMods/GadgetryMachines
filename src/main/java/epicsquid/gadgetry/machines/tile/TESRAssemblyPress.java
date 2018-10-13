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

public class TESRAssemblyPress extends TileEntitySpecialRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESRAssemblyPress() {
    super();
  }

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
    if (tile instanceof TileAssemblyPress) {
      TileAssemblyPress t = (TileAssemblyPress) tile;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder b = tess.getBuffer();

      GlStateManager.pushMatrix();
      GlStateManager.translate(x + 0.5, y + 0.25, z + 0.5);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/assembly_press.png"));
      GlStateManager.disableCull();

      float phase = 0f;
      if (t.progress[0] > 0) {
        phase += ((float) (t.progress[0])) * 9f;
      }
      double aCoeff = Math.pow(2.0f * (0.25 + 0.25 * Math.sin(Math.toRadians(phase))), 2.0f) * 0.5f;
      GlStateManager.pushMatrix();
      b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      Vec4d sideUV = new Vec4d(0, aCoeff, 0.5, 0.5 - aCoeff);
      Vec4d topUV = new Vec4d(0, 0.5, 0.5, 0.5);
      Primitives
          .addCubeToBuffer(b, -0.25, 0.5, -0.25, 0.25, aCoeff, 0.25, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true,
              true, true, true, true);
      tess.draw();
      GlStateManager.popMatrix();

      GlStateManager.enableCull();
      GlStateManager.popMatrix();
    }
  }
}
