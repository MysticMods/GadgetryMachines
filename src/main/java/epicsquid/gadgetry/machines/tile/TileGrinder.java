package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEHoriz;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateEmpty;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import epicsquid.gadgetry.machines.inventory.predicates.PredicateGrindable;
import epicsquid.gadgetry.machines.recipe.GrindingRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileGrinder extends TileModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String INVENTORY = "inventory";
  public float angle = -1;
  public int[] progress = { 0, 100 };

  public TileGrinder() {
    addModule(new ModuleInventory(INVENTORY, this, 2, "grinder", new int[] { 0 }, new int[] { 1 }) {
      @Override
      public boolean canInsertToSlot(int slot) {
        return slot != 1;
      }

      @Override
      public boolean canExtractFromSlot(int slot) {
        return true;
      }
    }.setSlotPredicate(0, new PredicateGrindable()).setSlotPredicate(1, new PredicateEmpty()));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public void update() {
    if (angle == -1) {
      IBlockState state = world.getBlockState(getPos());
      angle = state.getValue(BlockTEHoriz.facing).getHorizontalAngle() + 180f;
    }
    if (!world.isRemote) {
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      if (energy.battery.getEnergyStored() >= 20) {
        IInventory inv = (IInventory) modules.get(INVENTORY);
        if (!inv.getStackInSlot(0).isEmpty()) {
          ItemStack recipeOutput = ItemStack.EMPTY;
          GrindingRecipe r = GrindingRecipe.findRecipe(inv.getStackInSlot(0));
          if (r != null) {
            recipeOutput = r.getOutput();
          }
          if (r != null && inv.getStackInSlot(0).getCount() >= RecipeBase.getCount(r.inputs.get(0)) && (inv.getStackInSlot(1).isEmpty()
              || RecipeBase.stackMatches(recipeOutput, inv.getStackInSlot(1))
              && inv.getStackInSlot(1).getCount() <= inv.getStackInSlot(1).getMaxStackSize() - recipeOutput.getCount())) {
            energy.battery.extractEnergy(20, false);
            progress[0]++;
            if (progress[0] >= progress[1]) {
              progress[0] = 0;
              inv.decrStackSize(0, RecipeBase.getCount(r.inputs.get(0)));
              if (inv.getStackInSlot(1).isEmpty()) {
                inv.setInventorySlotContents(1, recipeOutput);
              } else {
                inv.getStackInSlot(1).grow(recipeOutput.getCount());
              }
            }
            markDirty();
          } else if (progress[0] > 0) {
            progress[0] = 0;
            markDirty();
          }
        } else if (progress[0] > 0) {
          progress[0] = 0;
          markDirty();
        }
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != GadgetryMachinesContent.grinder;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setInteger("progress", progress[0]);
    tag.setInteger("maxProgress", progress[1]);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    progress[0] = tag.getInteger("progress");
    progress[1] = tag.getInteger("maxProgress");
  }
}
