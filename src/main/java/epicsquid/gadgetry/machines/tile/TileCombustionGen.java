package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEOnOffHoriz;
import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.fluid.predicate.PredicateWhitelist;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.tile.TileMultiModular;
import epicsquid.gadgetry.core.util.Util;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import epicsquid.gadgetry.machines.recipe.CombustionValues;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileCombustionGen extends TileMultiModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String TANK = "tank";
  public float angle = -1;

  public TileCombustionGen() {
    addModule(new ModuleFluid(TANK, this, 125)
        .addTank(new ExtendedFluidTank(16000, new PredicateWhitelist(CombustionValues.values.keySet().toArray(new Fluid[0])), false))
        .addTank(new ExtendedFluidTank(16000, new PredicateWhitelist(FluidRegistry.WATER), true)));
    addModule(new ModuleEnergy(BATTERY, this, 640000, 6400, 6400));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public void update() {
    IBlockState state = world.getBlockState(getPos());
    ModuleFluid tank = (ModuleFluid) modules.get(TANK);
    FluidStack stack = tank.tanks.get(0).getFluid();
    FluidStack water = tank.tanks.get(1).getFluid();
    if (stack != null) {
      int amount = CombustionValues.getValue(tank.tanks.get(0).getFluid().getFluid());
      ModuleEnergy battery = (ModuleEnergy) modules.get(BATTERY);
      int inserted = battery.battery.receiveEnergy(amount, true);
      if (inserted > 0 && stack.amount > 0 && water != null && water.amount >= 4) {
        if (!world.isRemote) {
          tank.tanks.get(0).drain(1, true);
          tank.tanks.get(1).drain(4, true);
          battery.battery.receiveEnergy(amount, false);
          markDirty();
          if (!world.getBlockState(getPos()).getValue(BlockTEOnOffHoriz.active)) {
            world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, true), 8);
            world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, true), 8);
            world.notifyNeighborsOfStateChange(getPos(), state.getBlock(), true);
          }
        }
        if (Util.rand.nextInt(14) == 0) {
          spawnParticle(state);
        }
      } else if (!world.isRemote && world.getBlockState(getPos()).getValue(BlockTEOnOffHoriz.active)) {
        world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, false), 8);
        world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, false), 8);
        world.notifyNeighborsOfStateChange(getPos(), state.getBlock(), true);
      }
    } else if (!world.isRemote && world.getBlockState(getPos()).getValue(BlockTEOnOffHoriz.active)) {
      world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, false), 8);
      world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, false), 8);
      world.notifyNeighborsOfStateChange(getPos(), state.getBlock(), true);
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  public void spawnParticle(IBlockState state) {
    EnumFacing enumfacing = (EnumFacing) state.getValue(BlockTEOnOffHoriz.facing);
    double d0 = (double) pos.getX() + 0.5;
    double d1 = (double) pos.getY() + 0.5 + Util.rand.nextDouble() * 6.0 / 16.0;
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
    return super.shouldRefresh(world, pos, oldState, newState) && newState.getBlock() != GadgetryMachinesContent.combustion_gen;
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
