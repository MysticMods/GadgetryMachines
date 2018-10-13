package epicsquid.gadgetry.machines.gui;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.gui.ElementEnergyBar;
import epicsquid.gadgetry.core.lib.gui.ElementFluidTank;
import epicsquid.gadgetry.core.lib.gui.ElementHorizontalProgressBar;
import epicsquid.gadgetry.core.lib.gui.ElementToggleIOButton;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.gui.IGuiFactory;
import epicsquid.gadgetry.core.lib.tile.TileBase;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.machines.GadgetryMachines;
import epicsquid.gadgetry.machines.tile.TileOilWell;
import epicsquid.gadgetry.machines.tile.TilePump;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryOilWell implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TileOilWell) t).modules.get(TilePump.BATTERY))).addElement(
            new ElementHorizontalProgressBar(32, 34, 0, 32, 48, 16, 0, 48, ((TileOilWell) t).progress,
                new ResourceLocation(GadgetryMachines.MODID, "textures/gui/progress_bars.png")))
        .addElement(new ElementFluidTank(80, 10, ((ModuleFluid) ((TileOilWell) t).modules.get(TilePump.TANK)).manager.getTankProperties()[0]))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TileOilWell) t).TANK))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TileOilWell) t).BATTERY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileOilWell.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).initPlayerInventory(player.inventory, 0, 0);
  }

}
