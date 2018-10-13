package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileFluidPlacer extends TileModular implements ITickable {
  public static final String TANK = "tank";
  public int ticks = 0;

  public TileFluidPlacer() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(2000, new PredicateTrue(), true)));
    config.setAllIO(FaceIO.IN);
    BufferBuilder b;
    config.setAllModules(TANK);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      ticks++;
      if (ticks >= 20) {
        ticks = 0;
        ModuleFluid tank = (ModuleFluid) this.modules.get(TANK);
        BlockPos p = getPos().offset(world.getBlockState(getPos()).getValue(BlockTEFacing.facing));
        IBlockState state = world.getBlockState(p);
        FluidStack stack = tank.tanks.get(0).getFluid();
        if (stack != null && tank.tanks.get(0).getFluidAmount() >= 1000 && state.getBlock() != stack.getFluid().getBlock() && (state.getBlock() == Blocks.AIR
            || state.getBlock().isReplaceable(world, p))) {
          FluidStack toDrain = stack.copy();
          toDrain.amount = 1000;
          tank.manager.drain(toDrain, true);
          markDirty();
          IBlockState placeState = stack.getFluid().getBlock().getDefaultState();
          world.setBlockState(p, placeState);
          world.notifyBlockUpdate(p, state, placeState, 8);
          world.notifyNeighborsOfStateChange(p.north(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p.south(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p.east(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p.west(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p.up(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p.down(), stack.getFluid().getBlock(), true);
          world.notifyNeighborsOfStateChange(p, stack.getFluid().getBlock(), true);
        }
        markDirty();
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
  }
}
