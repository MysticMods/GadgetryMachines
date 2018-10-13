package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiAssemblyPress extends GuiModular {
  public ResourceLocation texture = new ResourceLocation("gadgetrymachines:textures/gui/big_container.png");

  public GuiAssemblyPress(ContainerModular inventorySlotsIn, int width, int height) {
    super(inventorySlotsIn, width, height);
  }

  @Override
  public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    Minecraft.getMinecraft().getTextureManager().bindTexture(baseTexture);
    drawElements(partialTicks, mouseX, mouseY);
  }

}
