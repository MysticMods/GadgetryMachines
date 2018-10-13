package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class TilePump extends TileModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String TANK = "tank";
  public float angle = -1;
  public int[] progress = { 0, 40 };

  public TilePump() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(8000, new PredicateTrue(), true)));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.IN);
  }

  public static FluidStack getFluid(World world, BlockPos pos, IBlockState state) {
    if (state.getBlock() instanceof IFluidBlock && ((IFluidBlock) state.getBlock()).canDrain(world, pos)) {
      return new FluidStack(((BlockFluidBase) state.getBlock()).getFluid(), 1000);
    }
    if (state.getBlock() instanceof BlockStaticLiquid) {
      if (state.getValue(BlockStaticLiquid.LEVEL) == 0) {
        if (state.getBlock() == Blocks.WATER) {
          return new FluidStack(FluidRegistry.WATER, 1000);
        }
        if (state.getBlock() == Blocks.LAVA) {
          return new FluidStack(FluidRegistry.LAVA, 1000);
        }
      }
    }

    return null;
  }

  public boolean attemptPump(BlockPos pos) {
    IBlockState state = world.getBlockState(pos);
    if (state.getBlock() instanceof IFluidBlock && ((IFluidBlock) state.getBlock()).canDrain(world, pos) || state.getBlock() instanceof BlockStaticLiquid) {
      FluidStack stack = getFluid(world, pos, state);
      if (stack != null) {
        ModuleFluid tank = (ModuleFluid) modules.get(TANK);
        int filled = tank.manager.fill(stack, false);
        if (filled == stack.amount) {
          if (!world.isRemote) {
            tank.manager.fill(stack, true);
          }
          markDirty();
          world.setBlockToAir(pos);
          world.notifyBlockUpdate(pos, state, Blocks.AIR.getDefaultState(), 8);
          world.notifyNeighborsOfStateChange(pos.north(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos.south(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos.east(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos.west(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos.up(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos.down(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(pos, Blocks.AIR, true);
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      ModuleFluid tank = (ModuleFluid) modules.get(TANK);
      if (energy.battery.getEnergyStored() >= 10 && tank.tanks.get(0).getFluidAmount() <= tank.tanks.get(0).getCapacity() - 1000) {
        energy.battery.extractEnergy(10, false);
        progress[0]++;
        if (progress[0] >= progress[1]) {
          progress[0] = 0;
          boolean doContinue = true;
          for (int r = 0; r < 6 && doContinue; r++) {
            for (int i = -r; i < r + 1 && doContinue; i++) {
              for (int j = -r; j < 1 && doContinue; j++) {
                for (int k = -r; k < r + 1 && doContinue; k++) {
                  doContinue = attemptPump(getPos().add(i, j - 1, k));
                }
              }
            }
          }
        }
        markDirty();
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
