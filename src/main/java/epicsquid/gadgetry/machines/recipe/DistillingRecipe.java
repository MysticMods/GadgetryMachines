package epicsquid.gadgetry.machines.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

public class DistillingRecipe extends RecipeBase {
  public static ArrayList<DistillingRecipe> recipes = new ArrayList<DistillingRecipe>();

  public static Set<Fluid> distillable_fluids = new HashSet<Fluid>();
  public static Set<Object> distillable_items = new HashSet<Object>();

  public FluidStack fluid_in = null;
  FluidStack fluid_out = null;

  @Nullable
  public static DistillingRecipe findRecipe(FluidStack fluid, ItemStack input) {
    for (int i = 0; i < recipes.size(); i++) {
      if (recipes.get(i).matches(fluid, input)) {
        return recipes.get(i);
      }
    }
    return null;
  }

  public DistillingRecipe(FluidStack fluid_in, FluidStack fluid_out, Object item_in, ItemStack item_out) {
    super(item_out, item_in);
    this.fluid_in = fluid_in;
    this.fluid_out = fluid_out;
  }

  public FluidStack getFluidResult(ItemStack input) {
    return fluid_out;
  }

  public static void registerAll() {
    if (Loader.isModLoaded("jei")) {
      MinecraftForge.EVENT_BUS.register(new DistillingRecipeJEI());
    }
    recipes.add(new DistillingRecipe(new FluidStack(GadgetryMachinesContent.oil, 1000), new FluidStack(GadgetryMachinesContent.fuel, 1000), ItemStack.EMPTY,
        ItemStack.EMPTY));
    recipes.add(
        new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 250), new ItemStack(Items.WHEAT, 1),
            new ItemStack(GadgetryMachinesContent.biomass, 1)));
    recipes.add(
        new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 125), new ItemStack(Items.POTATO, 1),
            new ItemStack(GadgetryMachinesContent.biomass, 1)));
    recipes.add(
        new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 250), new ItemStack(Items.BEETROOT, 1),
            new ItemStack(GadgetryMachinesContent.biomass, 1)));
    recipes.add(
        new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 500), new ItemStack(Items.REEDS, 1),
            new ItemStack(GadgetryMachinesContent.biomass, 1)));
    recipes.add(new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 500),
        new ItemStack(Blocks.BROWN_MUSHROOM, 1), new ItemStack(GadgetryMachinesContent.biomass, 1)));
    recipes.add(new DistillingRecipe(new FluidStack(FluidRegistry.WATER, 1000), new FluidStack(GadgetryMachinesContent.ethanol, 500),
        new ItemStack(Blocks.RED_MUSHROOM, 1), new ItemStack(GadgetryMachinesContent.biomass, 1)));
    for (DistillingRecipe r : recipes) {
      distillable_items.add(r.inputs.get(0));
      distillable_fluids.add(r.fluid_in.getFluid());
    }
  }

  public boolean matches(FluidStack fluid, ItemStack stack) {
    if (fluid == null ^ fluid_in == null)
      return false;
    return (stack.isEmpty() && this.inputs.get(0) instanceof ItemStack && ((ItemStack) inputs.get(0)).isEmpty() || super.matches(new ItemStack[] { stack }))
        && ((fluid == null && fluid_in == null || fluid.amount == 0 && (fluid_in.amount == 0 || fluid_in == null))
        || fluid.getFluid().getName() == fluid_in.getFluid().getName() && fluid.amount >= fluid_in.amount);
  }
}
