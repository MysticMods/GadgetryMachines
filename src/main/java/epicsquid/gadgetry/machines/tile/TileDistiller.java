package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateEmpty;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.core.tile.TileMultiModular;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import epicsquid.gadgetry.machines.inventory.predicates.PredicateItemDistillable;
import epicsquid.gadgetry.machines.recipe.DistillingRecipe;
import epicsquid.gadgetry.machines.tank.predicates.PredicateFluidDistillable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class TileDistiller extends TileMultiModular implements ITickable {
  public static final String BATTERY = "battery";
  public static final String TANK = "tank";
  public static final String INVENTORY = "inventory";
  public int[] progress = new int[] { 0, 400 };

  public TileDistiller() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(16000, new PredicateFluidDistillable(), false))
        .addTank(new ExtendedFluidTank(16000, new PredicateTrue(), true)));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    addModule(new ModuleInventory(INVENTORY, this, 2, "distiller", new int[] { 0 }, new int[] { 1 }) {
      @Override
      public boolean canInsertToSlot(int slot) {
        return slot != 1;
      }

      @Override
      public boolean canExtractFromSlot(int slot) {
        return true;
      }
    }.setSlotPredicate(0, new PredicateItemDistillable()).setSlotPredicate(1, new PredicateEmpty()));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public void update() {
    IInventory inv = ((IInventory) modules.get(INVENTORY));
    ExtendedFluidTank tank = ((ModuleFluid) modules.get(TANK)).tanks.get(0);
    ExtendedFluidTank output = ((ModuleFluid) modules.get(TANK)).tanks.get(1);
    ModuleEnergy e = ((ModuleEnergy) modules.get(BATTERY));
    if (!world.isRemote) {
      FluidStack f = tank.getFluid();
      ItemStack i = inv.getStackInSlot(0).copy();
      DistillingRecipe r = DistillingRecipe.findRecipe(f, i);
      if (r != null) {
        if (e.battery.getEnergyStored() >= 10 && (inv.getStackInSlot(1).isEmpty() || r.getOutput().isEmpty()
            || RecipeBase.stackMatches(inv.getStackInSlot(1), r.getOutput()) && inv.getStackInSlot(1).getCount() + r.getOutput().getCount() <= r.getOutput()
            .getMaxStackSize()) && (r.getFluidResult(i) == null || output.fill(r.getFluidResult(i), false) == r.getFluidResult(i).amount)) {
          e.battery.extractEnergy(10, false);
          progress[0]++;
          if (progress[0] >= progress[1]) {
            inv.decrStackSize(0, 1);
            if (inv.getStackInSlot(1).isEmpty())
              inv.setInventorySlotContents(1, r.getOutput());
            else
              inv.getStackInSlot(1).grow(r.getOutput().getCount());
            tank.drain(r.fluid_in, true);
            output.fill(r.getFluidResult(i), true);
            progress[0] = 0;
          }
          markDirty();
        }
      } else if (progress[0] > 0) {
        progress[0] = 0;
        markDirty();
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != GadgetryMachinesContent.combustion_gen;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setInteger("progress", progress[0]);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    progress[0] = tag.getInteger("progress");
  }
}
