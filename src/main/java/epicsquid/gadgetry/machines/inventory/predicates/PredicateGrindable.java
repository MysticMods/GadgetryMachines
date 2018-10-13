package epicsquid.gadgetry.machines.inventory.predicates;

import java.util.Set;
import java.util.function.Predicate;

import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.machines.recipe.GrindingRecipe;
import net.minecraft.item.ItemStack;

public class PredicateGrindable implements Predicate<ItemStack> {
  Set<Object> valid;

  public PredicateGrindable() {
    valid = GrindingRecipe.grindables;
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
