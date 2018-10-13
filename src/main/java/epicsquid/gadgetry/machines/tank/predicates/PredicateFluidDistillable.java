package epicsquid.gadgetry.machines.tank.predicates;

import java.util.Set;
import java.util.function.Predicate;

import epicsquid.gadgetry.machines.recipe.DistillingRecipe;
import net.minecraftforge.fluids.Fluid;

public class PredicateFluidDistillable implements Predicate<Fluid> {
  Set<Fluid> valid;

  public PredicateFluidDistillable() {
    valid = DistillingRecipe.distillable_fluids;
  }

  @Override
  public boolean test(Fluid arg0) {
    for (Fluid f : valid) {
      if (arg0.getName() == f.getName()) {
        return true;
      }
    }
    return false;
  }

}
