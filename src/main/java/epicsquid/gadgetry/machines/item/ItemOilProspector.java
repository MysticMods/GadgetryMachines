package epicsquid.gadgetry.machines.item;

import epicsquid.gadgetry.core.lib.gui.IHUDContainer;
import epicsquid.gadgetry.core.lib.item.ItemBase;
import epicsquid.gadgetry.machines.tile.TileOilWell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOilProspector extends ItemBase {

  public ItemOilProspector(String name) {
    super(name);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onGameOverlayRender(RenderGameOverlayEvent.Post e) {
    int w = e.getResolution().getScaledWidth();
    int h = e.getResolution().getScaledHeight();
    EntityPlayer player = Minecraft.getMinecraft().player;
    if (player.getHeldItemMainhand().getItem() instanceof ItemOilProspector || player.getHeldItemOffhand().getItem() instanceof ItemOilProspector) {
      World world = player.getEntityWorld();
      float density = 0.2f + 0.8f * TileOilWell.oilDensity(world, player.getPosition());
      GlStateManager.translate(w / 2, h / 2, 0);
      String dstr = I18n.format("gadgetrymachines.hud.oil_density") + Math.round(density * 1000f) / 10f + "%";
      FontRenderer f = Minecraft.getMinecraft().fontRenderer;
      int width = f.getStringWidth(dstr);
      IHUDContainer.renderBox(-width / 2 - 3, 10, width + 6, f.FONT_HEIGHT + 6, 64, 64, 64);
      f.drawStringWithShadow(dstr, -width / 2, 14, 0xFFFFFF);
      GlStateManager.translate(-w / 2, -h / 2, 0);
    }
  }
}
