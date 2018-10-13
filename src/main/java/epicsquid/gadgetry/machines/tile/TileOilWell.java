package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.lib.util.NoiseGenUtil;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.tile.TileMultiModular;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class TileOilWell extends TileMultiModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String TANK = "tank";
  public float angle = -1;
  public int[] progress = { 0, 80 };
  public boolean active = false;

  public TileOilWell() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(16000, new PredicateTrue(), true)));
    addModule(new ModuleEnergy(BATTERY, this, 640000, 6400, 6400));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return super.getRenderBoundingBox().expand(0, 3, 0);
  }

  public static float oilDensity(World world, BlockPos pos) {
    return (float) Math.pow((32.0f * NoiseGenUtil.getOctave(world.getSeed() + world.provider.getDimension(), pos.getX(), pos.getZ(), 31) + 16.0f * NoiseGenUtil
        .getOctave(world.getSeed() + world.provider.getDimension(), pos.getX(), pos.getZ(), 21) + 8.0f * NoiseGenUtil
        .getOctave(world.getSeed() + world.provider.getDimension(), pos.getX(), pos.getZ(), 14) + 6.0f * NoiseGenUtil
        .getOctave(world.getSeed() + world.provider.getDimension(), pos.getX(), pos.getZ(), 7) + 2.0f * NoiseGenUtil
        .getOctave(world.getSeed() + world.provider.getDimension(), pos.getX(), pos.getZ(), 3)) / 64.0f, 2);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      ModuleFluid tank = (ModuleFluid) modules.get(TANK);
      int oilAmount = 40 + (int) (161f * oilDensity(world, pos));
      active = false;
      if (energy.battery.getEnergyStored() >= 80 && tank.tanks.get(0).getFluidAmount() <= tank.tanks.get(0).getCapacity() - oilAmount) {
        active = true;
        energy.battery.extractEnergy(80, false);
        progress[0]++;
        if (progress[0] >= progress[1]) {
          FluidStack stack = new FluidStack(GadgetryMachinesContent.oil, oilAmount);
          int filled = tank.manager.fill(stack, false);
          if (filled == stack.amount) {
            if (!world.isRemote) {
              tank.manager.fill(stack, true);
            }
          }
          progress[0] = 0;
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
    return newState.getBlock() != GadgetryMachinesContent.oil_well;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setInteger("progress", progress[0]);
    tag.setInteger("maxProgress", progress[1]);
    tag.setBoolean("active", active);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    progress[0] = tag.getInteger("progress");
    progress[1] = tag.getInteger("maxProgress");
    active = tag.getBoolean("active");
  }
}
