package epicsquid.gadgetry.machines.inventory.predicates;

import java.util.function.Predicate;

import epicsquid.gadgetry.machines.item.IBlockFilter;
import net.minecraft.item.ItemStack;

public class PredicateBlockFilter implements Predicate<ItemStack> {
  @Override
  public boolean test(ItemStack arg0) {
    return arg0.getItem() instanceof IBlockFilter;
  }

}
