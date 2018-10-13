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
import epicsquid.gadgetry.machines.GadgetryMachines;
import epicsquid.gadgetry.machines.tile.TilePoweredFurnace;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFactoryPoweredFurnace implements IGuiFactory {

  @SideOnly(Side.CLIENT)
  @Override
  public Gui constructGui(EntityPlayer player, TileEntity tile) {
    TileModular t = (TileModular) tile;
    return new GuiModular(new ContainerModular(t).tryAddSlot(0, 41, 34).tryAddSlot(1, 113, 34, true).initPlayerInventory(player.inventory, 0, 0), 176, 166)
        .addElement(new ElementHorizontalProgressBar(61, 34, 48, 16, 44, 16, 48, 0, ((TilePoweredFurnace) t).progress,
            new ResourceLocation(GadgetryMachines.MODID, "textures/gui/progress_bars.png")))
        .addElement(new ElementEnergyBar(10, 10, (ModuleEnergy) ((TilePoweredFurnace) t).modules.get(TilePoweredFurnace.BATTERY)))
        .addElement(new ElementToggleIOButton(152, 60, (TileModular) t, ((TilePoweredFurnace) t).BATTERY))
        .addElement(new ElementToggleIOButton(135, 60, (TileModular) t, ((TilePoweredFurnace) t).INVENTORY));
  }

  @Override
  public String getName() {
    return TileBase.getTileName(TilePoweredFurnace.class);
  }

  @Override
  public Container constructContainer(EntityPlayer player, TileEntity tile) {
    return new ContainerModular((TileModular) tile).tryAddSlot(0, 41, 32).tryAddSlot(1, 113, 32, true).initPlayerInventory(player.inventory, 0, 0);
  }

}
