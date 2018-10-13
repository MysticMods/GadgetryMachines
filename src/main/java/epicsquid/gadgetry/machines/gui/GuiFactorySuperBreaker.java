package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.machines.tile.TileSuperBreaker;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactorySuperBreaker implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 31, 23).tryAddSlot(1, 49, 23).tryAddSlot(2, 31, 41).tryAddSlot(3, 49, 41).tryAddSlot(4, 76, 14)
        .tryAddSlot(5, 94, 14).tryAddSlot(6, 112, 14).tryAddSlot(7, 76, 32).tryAddSlot(8, 94, 32).tryAddSlot(9, 112, 32).tryAddSlot(10, 76, 50)
        .tryAddSlot(11, 94, 50).tryAddSlot(12, 112, 50).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(8, 10, (ModuleEnergy) ((TileSuperBreaker) t).modules.get(TileSuperBreaker.BATTERY)))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TileSuperBreaker) t).INVENTORY))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileSuperBreaker) t).BATTERY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileSuperBreaker.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 31, 23).tryAddSlot(1, 49, 23).tryAddSlot(2, 31, 41).tryAddSlot(3, 49, 41)
        .tryAddSlot(4, 76, 14).tryAddSlot(5, 94, 14).tryAddSlot(6, 112, 14).tryAddSlot(7, 76, 32).tryAddSlot(8, 94, 32).tryAddSlot(9, 112, 32)
        .tryAddSlot(10, 76, 50).tryAddSlot(11, 94, 50).tryAddSlot(12, 112, 50).initPlayerInventory(player.inventory, 0, 0);
  }

}
