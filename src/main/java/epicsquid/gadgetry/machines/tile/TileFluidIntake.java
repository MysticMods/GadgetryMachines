package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileFluidIntake extends TileModular implements ITickable {
  public static final String TANK = "tank";
  public int ticks = 0;

  public TileFluidIntake() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(2000, new PredicateTrue(), true)));
    config.setAllIO(FaceIO.OUT);
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
        FluidStack stack = TilePump.getFluid(world, p, state);
        if (stack != null && tank.manager.fill(stack, false) >= 1000) {
          tank.manager.fill(stack, true);
          markDirty();
          world.setBlockToAir(p);
          world.notifyBlockUpdate(p, state, Blocks.AIR.getDefaultState(), 8);
          world.notifyNeighborsOfStateChange(p.north(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p.south(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p.east(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p.west(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p.up(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p.down(), Blocks.AIR, true);
          world.notifyNeighborsOfStateChange(p, Blocks.AIR, true);
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
