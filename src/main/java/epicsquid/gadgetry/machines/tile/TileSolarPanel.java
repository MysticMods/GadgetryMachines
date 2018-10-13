package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileSolarPanel extends TileModular implements ITickable {
  public static final String BATTERY = "battery";

  public float light = 0;
  public int ticks = 0;

  public TileSolarPanel() {
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.OUT);
    config.setAllModules(BATTERY);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      ticks++;
      if (ticks >= 20) {
        ticks = 0;
        light = world.canBlockSeeSky(getPos().up()) ? (4 * world.getSunBrightnessFactor(0)) : 0;
      }
      ModuleEnergy battery = (ModuleEnergy) this.modules.get(BATTERY);
      battery.battery.receiveEnergy((int) light, false);
      markDirty();
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != GadgetryMachinesContent.furnace_gen;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setFloat("light", light);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    light = tag.getFloat("light");
  }
}
