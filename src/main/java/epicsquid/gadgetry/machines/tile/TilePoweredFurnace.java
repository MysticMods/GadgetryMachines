package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEOnOffHoriz;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateEmpty;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateSmeltable;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.core.util.Util;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePoweredFurnace extends TileModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String INVENTORY = "inventory";

  public int[] progress = { 0, 100 };

  public TilePoweredFurnace() {
    addModule(new ModuleInventory(INVENTORY, this, 2, "powered_furnace", new int[] { 0 }, new int[] { 1 }) {
      @Override
      public boolean canInsertToSlot(int slot) {
        return slot != 1;
      }

      @Override
      public boolean canExtractFromSlot(int slot) {
        return true;
      }
    }.setSlotPredicate(0, new PredicateSmeltable()).setSlotPredicate(1, new PredicateEmpty()));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public void update() {
    IBlockState state = world.getBlockState(getPos());
    if (!world.isRemote) {
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      IInventory inv = (IInventory) modules.get(INVENTORY);
      ItemStack recipeOutput = FurnaceRecipes.instance().getSmeltingResult(inv.getStackInSlot(0)).copy();
      if (energy.battery.getEnergyStored() >= 20) {
        if (!inv.getStackInSlot(0).isEmpty()) {
          if (recipeOutput != null && (inv.getStackInSlot(1).isEmpty() || RecipeBase.stackMatches(recipeOutput, inv.getStackInSlot(1))
              && inv.getStackInSlot(1).getCount() <= inv.getStackInSlot(1).getMaxStackSize() - recipeOutput.getCount())) {
            energy.battery.extractEnergy(20, false);
            if (!state.getValue(BlockTEOnOffHoriz.active) && progress[0] > 3) {
              world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, true), 8);
              world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, true), 8);
            }
            progress[0]++;
            if (progress[0] >= progress[1]) {
              progress[0] = 0;
              inv.decrStackSize(0, 1);
              if (inv.getStackInSlot(0).isEmpty()) {
                if (state.getValue(BlockTEOnOffHoriz.active)) {
                  world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, false), 8);
                  world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, false), 8);
                }
              }
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
    if (progress[0] > 0 && Util.rand.nextInt(14) == 0) {
      spawnParticle(state);
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  public void spawnParticle(IBlockState state) {
    EnumFacing enumfacing = (EnumFacing) state.getValue(BlockTEOnOffHoriz.facing);
    double d0 = (double) pos.getX() + 0.5;
    double d1 = (double) pos.getY() + Util.rand.nextDouble() * 6.0 / 16.0;
    double d2 = (double) pos.getZ() + 0.5;
    double d3 = 0.52;
    double d4 = Util.rand.nextDouble() * 0.6D - 0.3D;

    if (Util.rand.nextDouble() < 0.1D) {
      world.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS,
          1.0F, 1.0F, false);
    }

    switch (enumfacing) {
    case WEST:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      break;
    case EAST:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      break;
    case NORTH:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
      break;
    case SOUTH:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != GadgetryMachinesContent.powered_furnace;
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
