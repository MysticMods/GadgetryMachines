package epicsquid.gadgetry.machines.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

public class CombustionValues {
  public static Map<Fluid, Integer> values = new HashMap<>();

  public static int getValue(Fluid f) {
    for (Entry<Fluid, Integer> e : values.entrySet()) {
      if (e.getKey().getName() == f.getName()) {
        return e.getValue();
      }
    }
    return 0;
  }

  public static void init() {
    if (Loader.isModLoaded("jei")) {
      MinecraftForge.EVENT_BUS.register(new CombustionValuesJEI());
    }
    values.put(GadgetryMachinesContent.ethanol, 80);
    values.put(GadgetryMachinesContent.oil, 80);
    values.put(GadgetryMachinesContent.fuel, 200);
  }
}
