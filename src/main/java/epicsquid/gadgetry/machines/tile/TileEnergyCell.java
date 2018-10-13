package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import net.minecraft.util.ITickable;

public class TileEnergyCell extends TileModular implements ITickable {

  public static final String BATTERY = "battery";

  public TileEnergyCell() {
    addModule(new ModuleEnergy(BATTERY, this, 480000, 16000, 16000));
    config.setAllIO(FaceIO.IN);
    config.setAllModules(BATTERY);
  }

  @Override
  public void update() {
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }
}
