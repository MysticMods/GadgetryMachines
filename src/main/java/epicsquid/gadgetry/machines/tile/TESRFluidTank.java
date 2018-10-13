package epicsquid.gadgetry.machines.tile;

import org.lwjgl.opengl.GL11;

import epicsquid.gadgetry.core.lib.util.FluidTextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TESRFluidTank extends TileEntitySpecialRenderer {
  int blue, green, red, alpha;
  int lightx, lighty;
  double minU, minV, maxU, maxV, diffU, diffV;

  public TESRFluidTank() {
    super();
  }

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
    if (tile instanceof TileFluidTank) {
      TileFluidTank t = (TileFluidTank) tile;
      IFluidTankProperties tank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).getTankProperties()[0];
      if (tank.getContents() != null) {
        int amount = tank.getContents().amount;
        int capacity = tank.getCapacity();
        Fluid fluid = tank.getContents().getFluid();
        if (fluid != null) {
          int c = fluid.getColor();
          blue = c & 0xFF;
          green = (c >> 8) & 0xFF;
          red = (c >> 16) & 0xFF;
          alpha = (c >> 24) & 0xFF;

          TextureAtlasSprite sprite = FluidTextureUtil.stillTextures.get(fluid);
          diffU = sprite.getMaxU() - sprite.getMinU();
          diffV = sprite.getMaxV() - sprite.getMinV();

          if (sprite != null) {
            minU = sprite.getMinU() + diffU * 0.0625;
            maxU = sprite.getMaxU() - diffU * 0.0625;
            minV = sprite.getMinV() + diffV * 0.0625;
            maxV = sprite.getMaxV() - diffV * 0.0625;
            double minSideV = sprite.getMinV() + diffV * 0.0625;
            double maxSideV = sprite.getMinV() + diffV * (0.0625 + 0.875 * ((float) amount / (float) capacity));

            int i = getWorld().getCombinedLight(tile.getPos(), fluid.getLuminosity());
            lightx = i >> 0x10 & 0xFFFF;
            lighty = i & 0xFFFF;

            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(minU, minV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(maxU, minV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(maxU, maxV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(minU, maxV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();

            buffer.pos(x + 0.0625, y + 0.0625, z + 0.0625).tex(minU, minV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625, z + 0.0625).tex(maxU, minV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625, z + 0.9375).tex(maxU, maxV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625, z + 0.9375).tex(minU, maxV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();

            buffer.pos(x + 0.0625, y + 0.0625, z + 0.0625).tex(minU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625, z + 0.0625).tex(maxU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(maxU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(minU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();

            buffer.pos(x + 0.0625, y + 0.0625, z + 0.9375).tex(minU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625, z + 0.9375).tex(maxU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(maxU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(minU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();

            buffer.pos(x + 0.0625, y + 0.0625, z + 0.0625).tex(minU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625, z + 0.9375).tex(maxU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(maxU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.0625, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(minU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();

            buffer.pos(x + 0.9375, y + 0.0625, z + 0.0625).tex(minU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625, z + 0.9375).tex(maxU, minSideV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.9375).tex(maxU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            buffer.pos(x + 0.9375, y + 0.0625 + 0.875 * ((float) amount / (float) capacity), z + 0.0625).tex(minU, maxSideV).lightmap(lightx, lighty)
                .color(red, green, blue, alpha).endVertex();
            tess.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
          }
        }
      }
    }
  }
}
