package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementHorizontalProgressBar;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.machines.tile.TilePoweredAlloyer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryPoweredAlloyer implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 40, 34).tryAddSlot(1, 60, 34).tryAddSlot(2, 80, 34).tryAddSlot(3, 144, 34, true)
        .initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TilePoweredAlloyer) t).modules.get(TilePoweredAlloyer.BATTERY)))
        .addElement(new ElementHorizontalProgressBar(108, 34, 176, 16, 24, 16, 176, 0, ((TilePoweredAlloyer) t).progress))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TilePoweredAlloyer) t).BATTERY))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TilePoweredAlloyer) t).INVENTORY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TilePoweredAlloyer.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 40, 34).tryAddSlot(1, 60, 34).tryAddSlot(2, 80, 34).tryAddSlot(3, 144, 34, true)
        .initPlayerInventory(player.inventory, 0, 0);
  }

}
