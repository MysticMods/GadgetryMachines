package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.ElementVerticalProgressBar;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.machines.tile.TileGenerator;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryFurnaceGen implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 80, 42).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementVerticalProgressBar(80, 25, 208, 0, 16, 14, 224, 0, ((TileGenerator) t).ticks))
        .addElement(new ElementEnergyBar(8, 10, (ModuleEnergy) ((TileGenerator) t).modules.get(TileGenerator.BATTERY)))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileGenerator) t).BATTERY))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TileGenerator) t).INVENTORY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileGenerator.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 80, 40).initPlayerInventory(player.inventory, 0, 0);
  }

}
