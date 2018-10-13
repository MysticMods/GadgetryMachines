package epicsquid.gadgetry.machines.recipe;

import epicsquid.gadgetry.core.RegistryManager;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.event.RegisterModRecipesEvent;
import epicsquid.gadgetry.machines.GadgetryMachines;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class MachinesRecipeRegistry {
  @SubscribeEvent
  public void onRegister(RegisterModRecipesEvent event) {
    registerOreDict();
    ELRegistry.setActiveMod(GadgetryMachines.MODID, GadgetryMachines.CONTAINER);
    GrindingRecipe.registerAll();
    CombustionValues.init();
    DistillingRecipe.registerAll();

    FurnaceRecipes.instance().addSmelting(GadgetryMachinesContent.dust_iron, new ItemStack(Items.IRON_INGOT, 1), 1f);
    FurnaceRecipes.instance().addSmelting(GadgetryMachinesContent.dust_gold, new ItemStack(Items.GOLD_INGOT, 1), 1f);
    FurnaceRecipes.instance().addSmelting(GadgetryMachinesContent.dust_redmetal, new ItemStack(RegistryManager.redmetal_ingot, 1), 1f);
    FurnaceRecipes.instance().addSmelting(GadgetryMachinesContent.dust_steel, new ItemStack(RegistryManager.steel_ingot, 1), 1f);

    registerShaped(event.getRegistry(), "generator", new ItemStack(GadgetryMachinesContent.furnace_gen, 1), "III", "IFI", "RRR", 'I', "ingotIron", 'F',
        Blocks.FURNACE, 'R', "ingotRedmetal");
    registerShaped(event.getRegistry(), "energy_cell", new ItemStack(GadgetryMachinesContent.energy_cell, 1), "RIR", "R R", "RIR", 'I', "ingotIron", 'R',
        "ingotRedmetal");
    registerShaped(event.getRegistry(), "fluid_tank", new ItemStack(GadgetryMachinesContent.fluid_tank, 1), "GIG", "G G", "GIG", 'I', "ingotIron", 'G',
        "blockGlassColorless");
    registerShaped(event.getRegistry(), "powered_furnace", new ItemStack(GadgetryMachinesContent.powered_furnace, 1), "III", "I I", "CRC", 'I', "ingotIron",
        'R', "ingotRedmetal", 'C', "cobblestone");
    registerShaped(event.getRegistry(), "grinder", new ItemStack(GadgetryMachinesContent.grinder, 1), "S S", "SPS", "III", 'I', "ingotIron", 'S', "ingotSteel",
        'P', Blocks.HOPPER);
    registerShaped(event.getRegistry(), "solar_panel", new ItemStack(GadgetryMachinesContent.solar_panel, 1), "SSS", "SSS", "IRI", 'I', "ingotIron", 'S',
        "silicon", 'R', "ingotRedmetal");
    registerShaped(event.getRegistry(), "fluid_pump", new ItemStack(GadgetryMachinesContent.pump, 1), "III", " S ", "IPI", 'I', "ingotIron", 'S', "ingotSteel",
        'P', Blocks.PISTON);
    registerShaped(event.getRegistry(), "powered_alloyer", new ItemStack(GadgetryMachinesContent.powered_alloyer, 1), "III", "SFS", "SFS", 'I', "ingotIron",
        'S', "ingotSteel", 'F', Blocks.FURNACE);
    registerShaped(event.getRegistry(), "assembly_press", new ItemStack(GadgetryMachinesContent.assembly_press, 1), "ICI", "IWI", "III", 'I', "ingotIron", 'W',
        Blocks.CRAFTING_TABLE, 'C', "circuitBasic");
    registerShaped(event.getRegistry(), "circuit", new ItemStack(GadgetryMachinesContent.circuit, 1), "G G", " S ", " G ", 'G', "nuggetGold", 'S', "silicon");
    registerShaped(event.getRegistry(), "placer", new ItemStack(GadgetryMachinesContent.placer, 1), " I ", "I I", "RSR", 'S', "ingotSteel", 'I', "ingotIron",
        'R', "dustRedstone");
    registerShaped(event.getRegistry(), "breaker", new ItemStack(GadgetryMachinesContent.breaker, 1), " I ", "IPI", "RSR", 'S', "ingotSteel", 'I', "ingotIron",
        'R', "dustRedstone", 'P', Items.IRON_PICKAXE);
    registerShaped(event.getRegistry(), "activator", new ItemStack(GadgetryMachinesContent.activator, 1), " I ", "IcI", "RSR", 'S', "ingotSteel", 'c',
        "circuitBasic", 'I', "ingotIron", 'R', "dustRedstone");
    registerShaped(event.getRegistry(), "fluid_intake", new ItemStack(GadgetryMachinesContent.fluid_intake, 1), " H ", "I I", "RIR", 'H', Blocks.HOPPER, 'I',
        "ingotIron", 'R', "dustRedstone");
    registerShaped(event.getRegistry(), "fluid_placer", new ItemStack(GadgetryMachinesContent.fluid_placer, 1), " B ", "I I", "RIR", 'B', Items.BUCKET, 'I',
        "ingotIron", 'R', "dustRedstone");
    registerShaped(event.getRegistry(), "fuzzy_block_info", new ItemStack(GadgetryMachinesContent.fuzzy_block_info, 1), "L", "P", 'P', Items.PAPER, 'L',
        "dyeBlue");
    registerShaped(event.getRegistry(), "strict_block_info", new ItemStack(GadgetryMachinesContent.strict_block_info, 1), "L", "P", 'P', Items.PAPER, 'L',
        "dustRedstone");
    registerShaped(event.getRegistry(), "super_placer", new ItemStack(GadgetryMachinesContent.super_placer, 1), " C ", " P ", "G G", 'P',
        GadgetryMachinesContent.placer, 'G', "ingotGold", 'C', "circuitBasic");
    registerShaped(event.getRegistry(), "super_breaker", new ItemStack(GadgetryMachinesContent.super_breaker, 1), " C ", " P ", "G G", 'P',
        GadgetryMachinesContent.breaker, 'G', "ingotGold", 'C', "circuitBasic");
    registerShaped(event.getRegistry(), "super_activator", new ItemStack(GadgetryMachinesContent.super_activator, 1), " C ", " P ", "G G", 'P',
        GadgetryMachinesContent.activator, 'G', "ingotGold", 'C', "circuitBasic");
    registerShaped(event.getRegistry(), "distiller", new ItemStack(GadgetryMachinesContent.distiller, 1), "III", "ITI", "IRI", 'T',
        GadgetryMachinesContent.fluid_tank, 'I', "ingotIron", 'R', "ingotRedmetal");
    registerShaped(event.getRegistry(), "oil_well", new ItemStack(GadgetryMachinesContent.oil_well, 1), "SSS", " S ", "ICI", 'C', "circuitBasic", 'I',
        "ingotIron", 'S', "ingotSteel");
    registerShaped(event.getRegistry(), "oil_prospector", new ItemStack(GadgetryMachinesContent.oil_prospector, 1), "III", "IBI", " C ", 'C', "circuitBasic",
        'I', "ingotIron", 'B', Items.COAL);
    registerShaped(event.getRegistry(), "combustion_gen", new ItemStack(GadgetryMachinesContent.combustion_gen, 1), "III", "RDR", "RFR", 'F', Blocks.FURNACE,
        'I', "ingotIron", 'D', "dustRedstone", 'R', "ingotRedmetal");
    registerShaped(event.getRegistry(), "ranged_collector", new ItemStack(GadgetryMachinesContent.ranged_collector, 1), "I I", "IDI", " B ", 'B', Items.BUCKET,
        'I', "ingotIron", 'D', "dustRedstone");
    registerShaped(event.getRegistry(), "sprinkler", new ItemStack(GadgetryMachinesContent.sprinkler, 1), "III", "RBR", 'B', Items.BUCKET, 'R', "ingotRedmetal",
        'I', "ingotIron");
    registerShaped(event.getRegistry(), "drill", new ItemStack(GadgetryMachinesContent.drill, 1), "IS ", "CBS", "IS ", 'B', "blockSteel", 'S', "ingotSteel",
        'I', "ingotIron", 'C', "circuitBasic");
  }

  public static void registerOreDict() {
    OreDictionary.registerOre("dustGold", GadgetryMachinesContent.dust_gold);
    OreDictionary.registerOre("dustIron", GadgetryMachinesContent.dust_iron);
    OreDictionary.registerOre("dustRedmetal", GadgetryMachinesContent.dust_redmetal);
    OreDictionary.registerOre("dustSteel", GadgetryMachinesContent.dust_steel);
    OreDictionary.registerOre("coal", Items.COAL);
    OreDictionary.registerOre("sulfur", GadgetryMachinesContent.dust_sulfur);
    OreDictionary.registerOre("dustSulfur", GadgetryMachinesContent.dust_sulfur);
    OreDictionary.registerOre("circuitBasic", GadgetryMachinesContent.circuit);
  }

  public static ResourceLocation getRL(String s) {
    return new ResourceLocation(GadgetryMachines.MODID + ":" + s);
  }

  public static void registerShaped(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapedOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }

  public static void registerShapedMirrored(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapedOreRecipe(getRL(name), result, ingredients).setMirrored(true).setRegistryName(getRL(name)));
  }

  public static void registerShapeless(IForgeRegistry<IRecipe> registry, String name, ItemStack result, Object... ingredients) {
    registry.register(new ShapelessOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }
}
