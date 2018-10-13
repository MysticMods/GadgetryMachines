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
import epicsquid.gadgetry.machines.tile.TileDistiller;
import epicsquid.gadgetry.machines.tile.TilePump;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryDistiller implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 47, 36).tryAddSlot(1, 125, 36, true).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementHorizontalProgressBar(71, 36, 48, 48, 44, 16, 48, 32, ((TileDistiller) t).progress,
            new ResourceLocation(GadgetryMachines.MODID, "textures/gui/progress_bars.png")))
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TileDistiller) t).modules.get(TilePump.BATTERY)))
        .addElement(new ElementFluidTank(28, 10, ((ModuleFluid) ((TileDistiller) t).modules.get(TilePump.TANK)).manager.getTankProperties()[0]))
        .addElement(new ElementFluidTank(148, 10, ((ModuleFluid) ((TileDistiller) t).modules.get(TilePump.TANK)).manager.getTankProperties()[1]))
        .addElement(new ElementToggleIOButton(131, 60, (TileModular) t, ((TileDistiller) t).TANK))
        .addElement(new ElementToggleIOButton(114, 60, (TileModular) t, ((TileDistiller) t).BATTERY))
        .addElement(new ElementToggleIOButton(97, 60, (TileModular) t, ((TileDistiller) t).INVENTORY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TileDistiller.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 47, 36).tryAddSlot(1, 125, 36, true).initPlayerInventory(player.inventory, 0, 0);
  }

}
