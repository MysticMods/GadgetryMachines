package epicsquid.gadgetry.machines.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import epicsquid.gadgetry.core.lib.util.OreStack;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

public class GrindingRecipe extends RecipeBase {
  public static ArrayList<GrindingRecipe> recipes = new ArrayList<GrindingRecipe>();

  public static Set<Object> grindables = new HashSet<Object>();

  @Nullable
  public static GrindingRecipe findRecipe(ItemStack input) {
    for (int i = 0; i < recipes.size(); i++) {
      if (recipes.get(i).matches(new ItemStack[] { input })) {
        return recipes.get(i);
      }
    }
    return null;
  }

  public static void registerAll() {
    if (Loader.isModLoaded("jei")) {
      MinecraftForge.EVENT_BUS.register(new GrindingRecipeJEI());
    }
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_gold, 1), new OreStack("ingotGold", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_iron, 1), new OreStack("ingotIron", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_steel, 1), new OreStack("ingotSteel", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_redmetal, 1), new OreStack("ingotRedmetal", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_coal, 1), new OreStack("coal", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_gold, 2), new OreStack("oreGold", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_iron, 2), new OreStack("oreIron", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(GadgetryMachinesContent.dust_gold, 2), new OreStack("oreGold", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Items.COAL, 2), new OreStack("oreCoal", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Items.REDSTONE, 8), new OreStack("oreRedstone", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Items.DYE, 12, 4), new OreStack("oreLapis", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Items.DIAMOND, 2), new OreStack("oreDiamond", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Items.EMERALD, 2), new OreStack("oreEmerald", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Blocks.GRAVEL, 1), new OreStack("cobblestone", 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Blocks.SAND, 1), new ItemStack(Blocks.GRAVEL, 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Blocks.SAND, 4), new ItemStack(Blocks.SANDSTONE, 1)));
    recipes.add(new GrindingRecipe(new ItemStack(Blocks.SAND, 4, 1), new ItemStack(Blocks.RED_SANDSTONE, 1)));

    for (GrindingRecipe r : recipes) {
      grindables.add(r.inputs.get(0));
    }

    String[] keys = OreDictionary.getOreNames();
    for (String key : keys) {
      if (key.startsWith("dust")) {
        String material = key.substring(4);
        if (OreDictionary.doesOreNameExist("ore" + material) && !grindables.contains(new OreStack("ore" + material, 1))) {
          List<ItemStack> l = OreDictionary.getOres(key);
          if (l.size() > 0) {
            ItemStack result = l.get(0).copy();
            result.setCount(2);
            recipes.add(new GrindingRecipe(result, "ore" + material));
            grindables.add(new OreStack("ore" + material, 1));
          }
        }
        if (OreDictionary.doesOreNameExist("ingot" + material) && !grindables.contains(new OreStack("ingot" + material, 1))) {
          List<ItemStack> l = OreDictionary.getOres(key);
          if (l.size() > 0) {
            ItemStack result = l.get(0).copy();
            result.setCount(1);
            recipes.add(new GrindingRecipe(result, "ingot" + material));
            grindables.add(new OreStack("ingot" + material, 1));
          }
        }
        if (OreDictionary.doesOreNameExist("gem" + material) && !grindables.contains(new OreStack("gem" + material, 1))) {
          List<ItemStack> l = OreDictionary.getOres(key);
          if (l.size() > 0) {
            ItemStack result = l.get(0).copy();
            result.setCount(1);
            recipes.add(new GrindingRecipe(result, "gem" + material));
            grindables.add(new OreStack("gem" + material, 1));
          }
        }
      }
    }
  }

  public GrindingRecipe(ItemStack output, Object... inputs) {
    super(output, inputs);
  }
}
