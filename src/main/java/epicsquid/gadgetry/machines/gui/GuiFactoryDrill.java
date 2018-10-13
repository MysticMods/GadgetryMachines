package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementFluidTank;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.machines.tile.TileDrill;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryDrill implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 62, 14).tryAddSlot(1, 80, 14).tryAddSlot(2, 98, 14).tryAddSlot(3, 62, 32).tryAddSlot(4, 80, 32)
        .tryAddSlot(5, 98, 32).tryAddSlot(6, 62, 50).tryAddSlot(7, 80, 50).tryAddSlot(8, 98, 50).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TileDrill) t).modules.get(TileDrill.BATTERY)))
        .addElement(new ElementFluidTank(28, 10, ((ModuleFluid) ((TileDrill) t).modules.get(TileDrill.TANK)).manager.getTankProperties()[0]))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileDrill) t).TANK))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TileDrill) t).BATTERY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileDrill.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 62, 14).tryAddSlot(1, 80, 14).tryAddSlot(2, 98, 14).tryAddSlot(3, 62, 32)
        .tryAddSlot(4, 80, 32).tryAddSlot(5, 98, 32).tryAddSlot(6, 62, 50).tryAddSlot(7, 80, 50).tryAddSlot(8, 98, 50)
        .initPlayerInventory(player.inventory, 0, 0);
  }

}
