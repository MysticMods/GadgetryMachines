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
import epicsquid.gadgetry.machines.tile.TileCombustionGen;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryCombustionGen implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(80, 10, (ModuleEnergy) ((TileCombustionGen) t).modules.get(TileCombustionGen.BATTERY)))
        .addElement(new ElementFluidTank(10, 10, ((ModuleFluid) ((TileCombustionGen) t).modules.get(TileCombustionGen.TANK)).manager.getTankProperties()[0]))
        .addElement(new ElementFluidTank(28, 10, ((ModuleFluid) ((TileCombustionGen) t).modules.get(TileCombustionGen.TANK)).manager.getTankProperties()[1]))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileCombustionGen) t).TANK))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TileCombustionGen) t).BATTERY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileCombustionGen.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).initPlayerInventory(player.inventory, 0, 0);
  }

}
