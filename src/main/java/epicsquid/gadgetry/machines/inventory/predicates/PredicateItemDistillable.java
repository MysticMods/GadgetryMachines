package epicsquid.gadgetry.machines.inventory.predicates;

import java.util.Set;
import java.util.function.Predicate;

import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.machines.recipe.DistillingRecipe;
import net.minecraft.item.ItemStack;

public class PredicateItemDistillable implements Predicate<ItemStack> {
  Set<Object> valid;

  public PredicateItemDistillable() {
    valid = DistillingRecipe.distillable_items;
  }

  @Override
  public boolean test(ItemStack arg0) {
    for (Object o : valid) {
      if (RecipeBase.stackMatches(arg0, o)) {
        return true;
      }
    }
    return false;
  }

}
