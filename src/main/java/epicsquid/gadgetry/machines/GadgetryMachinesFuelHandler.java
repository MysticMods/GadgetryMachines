package epicsquid.gadgetry.machines;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class GadgetryMachinesFuelHandler implements IFuelHandler {

  @Override
  public int getBurnTime(ItemStack fuel) {
    if (fuel.getItem() == GadgetryMachinesContent.biomass) {
      return 400;
    }
    return 0;
  }

}
