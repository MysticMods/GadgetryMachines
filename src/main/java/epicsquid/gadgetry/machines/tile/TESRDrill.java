package epicsquid.gadgetry.machines.tile;

import org.lwjgl.opengl.GL11;

import epicsquid.gadgetry.core.lib.tile.IDelayedTileRenderer;
import epicsquid.gadgetry.core.util.Primitives;
import epicsquid.gadgetry.core.util.Vec4d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TESRDrill extends TileEntitySpecialRenderer<TileDrill> implements IDelayedTileRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESRDrill() {
    super();
  }

  @Override
  public void renderLater(TileEntity tile, double x, double y, double z, float partialTicks) {
    Minecraft.getMinecraft().entityRenderer.enableLightmap();

    TileDrill t = (TileDrill) tile;
    int i = t.getWorld().getCombinedLight(t.getPos().up(1).offset(t.face), 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
    Tessellator tess = Tessellator.getInstance();
    BufferBuilder b = tess.getBuffer();

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/drill_2.png"));
    GlStateManager.disableCull();

    float phase = -45.0f;
    if (t.active) {
      phase += ((float) ((float) t.anim + partialTicks)) * 22.5f;
    }

    rotateForFacing(t.face);
    GlStateManager.translate(0, 0.5, 0);
    GlStateManager.rotate(phase, 0, 1, 0);
    RenderHelper.disableStandardItemLighting();
    b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
    for (int ind = 0; ind < (t.distance - 2); ind++) {
      i = t.getWorld().getCombinedLight(t.getPos().up(1).offset(t.face, -ind), 1);
      j = i >> 16 & 65535;
      k = i & 65535;
      if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
        int i2 = t.getWorld().getCombinedLight(t.getPos().up(1).offset(t.face, -(ind + 1)), 1);
        int j2 = i2 >> 16 & 65535;
        int k2 = i2 & 65535;
        renderSegment(b, -0.25, (ind + 1), -0.25, 0.25, ind, 0.25, j2, k2, j, k);
      } else
        renderSegment(b, -0.25, (ind + 1), -0.25, 0.25, ind, 0.25, j, k, j, k);
    }
    tess.draw();
    GlStateManager.enableLighting();
    GlStateManager.enableLight(0);
    GlStateManager.enableLight(1);
    GlStateManager.enableColorMaterial();

    GlStateManager.translate(0, t.distance - 2, 0);

    i = t.getWorld().getCombinedLight(t.getPos().up(1).offset(t.face, -(t.distance - 1)), 0);
    j = i % 65536;
    k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

    b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    Vec4d sideUV = new Vec4d(0f / 64f, 60f / 64f, 32f / 64f, 4f / 64f);
    Vec4d topUV = new Vec4d(32f / 64f, 32f / 64f, 32f / 64f, 32f / 64f);
    Primitives
        .addCubeToBuffer(b, -1, 0.25, -1, 1, 0, 1, new Vec4d[] { sideUV, sideUV, topUV, topUV, sideUV, sideUV }, 1f, 1f, 1f, 1f, true, true, true, true, true,
            true);
    tess.draw();
    GlStateManager.translate(0, 0.25, 0);
    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("gadgetrymachines:textures/blocks/iron_drill.png"));
    b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    b.pos(-0.75, 0, -0.75).tex(0, 0.75).color(1f, 1f, 1f, 1f).normal(0, 0, -1).endVertex();
    b.pos(0.75, 0, -0.75).tex(0.75, 0.75).color(1f, 1f, 1f, 1f).normal(0, 0, -1).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(0, 0, -1).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(0, 0, -1).endVertex();

    b.pos(-0.75, 0, -0.75).tex(0, 0.75).color(1f, 1f, 1f, 1f).normal(-1, 0, 0).endVertex();
    b.pos(-0.75, 0, 0.75).tex(0.75, 0.75).color(1f, 1f, 1f, 1f).normal(-1, 0, 0).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(-1, 0, 0).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(-1, 0, 0).endVertex();

    b.pos(-0.75, 0, 0.75).tex(0, 0.75).color(1f, 1f, 1f, 1f).normal(0, 0, 1).endVertex();
    b.pos(0.75, 0, 0.75).tex(0.75, 0.75).color(1f, 1f, 1f, 1f).normal(0, 0, 1).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(0, 0, 1).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(0, 0, 1).endVertex();

    b.pos(0.75, 0, -0.75).tex(0, 0.75).color(1f, 1f, 1f, 1f).normal(1, 0, 0).endVertex();
    b.pos(0.75, 0, 0.75).tex(0.75, 0.75).color(1f, 1f, 1f, 1f).normal(1, 0, 0).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(1, 0, 0).endVertex();
    b.pos(0, 1.5, 0).tex(0.375, 0).color(1f, 1f, 1f, 1f).normal(1, 0, 0).endVertex();
    tess.draw();
    GlStateManager.popMatrix();

    GlStateManager.enableCull();
    Minecraft.getMinecraft().entityRenderer.disableLightmap();
  }

  Vec4d segmSideUV = new Vec4d(16f / 64f, 28f / 64f, 8f / 64f, 32f / 64f);

  public void renderSegment(BufferBuilder buff, double x1, double y1, double z1, double x2, double y2, double z2, int lx1, int ly1, int lx2, int ly2) {
    buff.pos(x1, y1, z1).tex(segmSideUV.x, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y1, z1).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y2, z1).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y2, z1).tex(segmSideUV.x, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();

    buff.pos(x2, y1, z2).tex(segmSideUV.x, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y1, z2).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y2, z2).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y2, z2).tex(segmSideUV.x, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();

    buff.pos(x2, y1, z2).tex(segmSideUV.x, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y1, z1).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y2, z1).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x2, y2, z2).tex(segmSideUV.x, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();

    buff.pos(x1, y1, z1).tex(segmSideUV.x, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y1, z2).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y).lightmap(lx1, ly1).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y2, z2).tex(segmSideUV.x + segmSideUV.z, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();
    buff.pos(x1, y2, z1).tex(segmSideUV.x, segmSideUV.y + segmSideUV.w).lightmap(lx2, ly2).color(1f, 1f, 1f, 1f).endVertex();
  }

  public static void rotateForFacing(EnumFacing face) {
    if (face.ordinal() == 1) {
      GlStateManager.rotate(180, 1, 0, 0);
    } else if (face.ordinal() > 1) {
      GlStateManager.rotate(180 - face.getHorizontalAngle(), 0, 1, 0);
      GlStateManager.rotate(90, 1, 0, 0);
    }
  }
}
