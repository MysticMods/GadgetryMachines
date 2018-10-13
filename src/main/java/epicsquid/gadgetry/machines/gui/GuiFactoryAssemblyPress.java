package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementHorizontalProgressBar;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.machines.tile.TileAssemblyPress;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryAssemblyPress implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiAssemblyPress(
        new ContainerModular(t).tryAddSlot(0, 8, 85).tryAddSlot(1, 26, 85).tryAddSlot(2, 44, 85).tryAddSlot(3, 62, 85).tryAddSlot(4, 80, 85)
            .tryAddSlot(5, 98, 85).tryAddSlot(6, 116, 85).tryAddSlot(7, 134, 85).tryAddSlot(8, 152, 85).tryAddSlot(9, 144, 34, true).tryAddSlot(10, 42, 16)
            .tryAddSlot(11, 60, 16).tryAddSlot(12, 78, 16).tryAddSlot(13, 42, 34).tryAddSlot(14, 60, 34).tryAddSlot(15, 78, 34).tryAddSlot(16, 42, 52)
            .tryAddSlot(17, 60, 52).tryAddSlot(18, 78, 52).tryAddSlot(19, 110, 12).initPlayerInventory(player.inventory, 0, 28), 176, 194)
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TileAssemblyPress) t).modules.get(TileAssemblyPress.BATTERY)))
        .addElement(new ElementHorizontalProgressBar(106, 34, 176, 16, 24, 16, 176, 0, ((TileAssemblyPress) t).progress));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileAssemblyPress.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 8, 85).tryAddSlot(1, 26, 85).tryAddSlot(2, 44, 85).tryAddSlot(3, 62, 85).tryAddSlot(4, 80, 85)
        .tryAddSlot(5, 98, 85).tryAddSlot(6, 116, 85).tryAddSlot(7, 134, 85).tryAddSlot(8, 152, 85).tryAddSlot(9, 144, 34, true).tryAddSlot(10, 42, 16)
        .tryAddSlot(11, 60, 16).tryAddSlot(12, 78, 16).tryAddSlot(13, 42, 34).tryAddSlot(14, 60, 34).tryAddSlot(15, 78, 34).tryAddSlot(16, 42, 52)
        .tryAddSlot(17, 60, 52).tryAddSlot(18, 78, 52).tryAddSlot(19, 110, 12).initPlayerInventory(player.inventory, 0, 28);
  }

}
