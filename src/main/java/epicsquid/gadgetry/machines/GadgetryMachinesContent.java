package epicsquid.gadgetry.machines;

import java.util.ArrayList;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.block.BlockTEHoriz;
import epicsquid.gadgetry.core.block.BlockTEOnOffHoriz;
import epicsquid.gadgetry.core.block.fluid.BlockFluid;
import epicsquid.gadgetry.core.block.fluid.FluidBase;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.block.BlockTEBase;
import epicsquid.gadgetry.core.lib.event.RegisterColorHandlersEvent;
import epicsquid.gadgetry.core.lib.event.RegisterContentEvent;
import epicsquid.gadgetry.core.lib.event.RegisterGuiFactoriesEvent;
import epicsquid.gadgetry.core.lib.gui.GuiHandler;
import epicsquid.gadgetry.core.lib.item.ItemBase;
import epicsquid.gadgetry.machines.block.BlockCombustionGen;
import epicsquid.gadgetry.machines.block.BlockDistiller;
import epicsquid.gadgetry.machines.block.BlockDrill;
import epicsquid.gadgetry.machines.block.BlockOilWell;
import epicsquid.gadgetry.machines.block.BlockSprinkler;
import epicsquid.gadgetry.machines.gui.*;
import epicsquid.gadgetry.machines.item.ItemDust;
import epicsquid.gadgetry.machines.item.ItemFuzzyBlockInfo;
import epicsquid.gadgetry.machines.item.ItemOilProspector;
import epicsquid.gadgetry.machines.item.ItemStrictBlockInfo;
import epicsquid.gadgetry.machines.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GadgetryMachinesContent {
  public static ArrayList<Item> items = new ArrayList<Item>();
  public static ArrayList<Block> blocks = new ArrayList<Block>();

  public static Item dust_iron, dust_gold, dust_redmetal, dust_steel, dust_coal, dust_sulfur, biomass, circuit, fuzzy_block_info, strict_block_info, oil_prospector;

  public static Block furnace_gen, energy_cell, fluid_tank, powered_furnace, grinder, solar_panel, pump, powered_alloyer, assembly_press, breaker, placer, activator, fluid_intake, fluid_placer, super_breaker, super_placer, super_activator, sprinkler, ranged_collector, oil_well, combustion_gen, distiller, drill;

  public static Block ethanol_block, oil_block, fuel_block;

  public static Fluid ethanol, oil, fuel;

  @SubscribeEvent
  public void registerContent(RegisterContentEvent event) {
    ELRegistry.setActiveMod(GadgetryMachines.MODID, GadgetryMachines.CONTAINER);

    GameRegistry.registerFuelHandler(new GadgetryMachinesFuelHandler());

    event.addItem(dust_iron = new ItemDust("dust_iron", 0x727272, 0xd8d8d8, 0xffffff).setCreativeTab(GadgetryMachines.tab));
    event.addItem(dust_gold = new ItemDust("dust_gold", 0xdc7613, 0xffff0b, 0xffffff).setCreativeTab(GadgetryMachines.tab));
    event.addItem(dust_redmetal = new ItemDust("dust_redmetal", 0xa13527, 0xff543d, 0xffc4bd).setCreativeTab(GadgetryMachines.tab));
    event.addItem(dust_steel = new ItemDust("dust_steel", 0x404246, 0x65696f, 0xacb2bc).setCreativeTab(GadgetryMachines.tab));
    event.addItem(dust_coal = new ItemDust("dust_coal", 0x050505, 0x212121, 0x404040).setCreativeTab(GadgetryMachines.tab));
    event.addItem(dust_sulfur = new ItemDust("dust_sulfur", 0xb59a4a, 0xffe261, 0xffecb8).setCreativeTab(GadgetryMachines.tab));
    event.addItem(biomass = new ItemBase("biomass").setCreativeTab(GadgetryMachines.tab));
    event.addItem(circuit = new ItemBase("circuit").setCreativeTab(GadgetryMachines.tab));
    event.addItem(fuzzy_block_info = new ItemFuzzyBlockInfo("fuzzy_block_info").setCreativeTab(GadgetryMachines.tab));
    event.addItem(strict_block_info = new ItemStrictBlockInfo("strict_block_info").setCreativeTab(GadgetryMachines.tab));
    event.addItem(oil_prospector = new ItemOilProspector("oil_prospector").setCreativeTab(GadgetryMachines.tab));

    FluidRegistry.registerFluid(ethanol = new FluidBase("ethanol", new ResourceLocation(GadgetryMachines.MODID + ":blocks/ethanol_still"),
        new ResourceLocation(GadgetryMachines.MODID + ":blocks/ethanol_flow"), ethanol_block).setDensity(750));
    FluidRegistry.registerFluid(oil = new FluidBase("oil", new ResourceLocation(GadgetryMachines.MODID + ":blocks/oil_still"),
        new ResourceLocation(GadgetryMachines.MODID + ":blocks/oil_flow"), oil_block).setDensity(2000).setViscosity(4000));
    FluidRegistry.registerFluid(fuel = new FluidBase("fuel", new ResourceLocation(GadgetryMachines.MODID + ":blocks/fuel_still"),
        new ResourceLocation(GadgetryMachines.MODID + ":blocks/fuel_flow"), fuel_block).setViscosity(1500));

    event.addBlock(furnace_gen = new BlockTEOnOffHoriz(Material.ROCK, SoundType.METAL, 2.4f, "furnace_gen", TileGenerator.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(energy_cell = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.4f, "energy_cell", TileEnergyCell.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(fluid_tank = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.0f, "fluid_tank", TileFluidTank.class) {
      @Override
      @SideOnly(Side.CLIENT)
      public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
      }
    }.setOpacity(false).setHarvestReqs("pickaxe", 0).setLightOpacity(0).setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        powered_furnace = new BlockTEOnOffHoriz(Material.ROCK, SoundType.METAL, 2.0f, "powered_furnace", TilePoweredFurnace.class).setHarvestReqs("pickaxe", 0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(grinder = new BlockTEHoriz(Material.ROCK, SoundType.METAL, 2.0f, "grinder", TileGrinder.class).setOpacity(false).setHarvestReqs("pickaxe", 0)
        .setLightOpacity(0).setCreativeTab(GadgetryMachines.tab));
    event.addBlock(solar_panel = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.4f, "solar_panel", TileSolarPanel.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        pump = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.4f, "pump", TilePump.class).setOpacity(false).setHarvestReqs("pickaxe", 0).setLightOpacity(0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        powered_alloyer = new BlockTEOnOffHoriz(Material.ROCK, SoundType.METAL, 2.0f, "powered_alloyer", TilePoweredAlloyer.class).setHarvestReqs("pickaxe", 0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(assembly_press = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.4f, "assembly_press", TileAssemblyPress.class).setOpacity(false)
        .setHarvestReqs("pickaxe", 0).setLightOpacity(0).setCreativeTab(GadgetryMachines.tab));
    event.addBlock(breaker = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "breaker", TileBreaker.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(placer = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "placer", TilePlacer.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(activator = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "activator", TileActivator.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(fluid_intake = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "fluid_intake", TileFluidIntake.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(fluid_placer = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "fluid_placer", TileFluidPlacer.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(super_breaker = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "super_breaker", TileSuperBreaker.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(super_placer = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "super_placer", TileSuperPlacer.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        super_activator = new BlockTEFacing(Material.ROCK, SoundType.METAL, 2.0f, "super_activator", TileSuperActivator.class).setHarvestReqs("pickaxe", 0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        sprinkler = new BlockSprinkler(Material.ROCK, SoundType.METAL, 2.0f, "sprinkler", TileSprinkler.class).setOpacity(false).setHarvestReqs("pickaxe", 0)
            .setLightOpacity(0).setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        ranged_collector = new BlockTEBase(Material.ROCK, SoundType.METAL, 2.4f, "ranged_collector", TileRangedCollector.class).setHarvestReqs("pickaxe", 0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(oil_well = new BlockOilWell(Material.ROCK, SoundType.METAL, 2.0f, "oil_well", TileOilWell.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        combustion_gen = new BlockCombustionGen(Material.ROCK, SoundType.METAL, 2.0f, "combustion_gen", TileCombustionGen.class).setHarvestReqs("pickaxe", 0)
            .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(distiller = new BlockDistiller(Material.ROCK, SoundType.METAL, 2.0f, "distiller", TileDistiller.class).setHarvestReqs("pickaxe", 0)
        .setCreativeTab(GadgetryMachines.tab));
    event.addBlock(
        drill = new BlockDrill(Material.ROCK, SoundType.METAL, 2.4f, "drill", TileDrill.class).setHarvestReqs("pickaxe", 0).setOpacity(false).setLightOpacity(0)
            .setCreativeTab(GadgetryMachines.tab));

    event.addBlock(ethanol_block = new BlockFluid(GadgetryMachines.MODID, "ethanol", false, Material.WATER, ethanol));
    FluidRegistry.addBucketForFluid(ethanol);
    event.addBlock(oil_block = new BlockFluid(GadgetryMachines.MODID, "oil", false, Material.WATER, oil));
    FluidRegistry.addBucketForFluid(oil);
    event.addBlock(fuel_block = new BlockFluid(GadgetryMachines.MODID, "fuel", false, Material.WATER, fuel));
    FluidRegistry.addBucketForFluid(fuel);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void registerColorHandlers(RegisterColorHandlersEvent event) {
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_coal).new DustColorHandler(), dust_coal);
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_iron).new DustColorHandler(), dust_iron);
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_redmetal).new DustColorHandler(), dust_redmetal);
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_gold).new DustColorHandler(), dust_gold);
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_steel).new DustColorHandler(), dust_steel);
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((ItemDust) dust_sulfur).new DustColorHandler(), dust_sulfur);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void registerRendering(ModelRegistryEvent event) {
    ClientRegistry.bindTileEntitySpecialRenderer(TileFluidTank.class, new TESRFluidTank());
    ClientRegistry.bindTileEntitySpecialRenderer(TileGrinder.class, new TESRGrinder());
    ClientRegistry.bindTileEntitySpecialRenderer(TilePump.class, new TESRPump());
    ClientRegistry.bindTileEntitySpecialRenderer(TileAssemblyPress.class, new TESRAssemblyPress());
    ClientRegistry.bindTileEntitySpecialRenderer(TileOilWell.class, new TESROilWell());
    ClientRegistry.bindTileEntitySpecialRenderer(TileDrill.class, new TESRDrill());
  }

  @SubscribeEvent
  public void onRegisterGuiFactories(RegisterGuiFactoriesEvent event) {
    GuiHandler.registerGui(new GuiFactoryEnergyCell());
    GuiHandler.registerGui(new GuiFactoryFluidTank());
    GuiHandler.registerGui(new GuiFactoryFurnaceGen());
    GuiHandler.registerGui(new GuiFactoryPoweredFurnace());
    GuiHandler.registerGui(new GuiFactoryGrinder());
    GuiHandler.registerGui(new GuiFactorySolarPanel());
    GuiHandler.registerGui(new GuiFactoryPump());
    GuiHandler.registerGui(new GuiFactoryPoweredAlloyer());
    GuiHandler.registerGui(new GuiFactoryAssemblyPress());
    GuiHandler.registerGui(new GuiFactoryBreaker());
    GuiHandler.registerGui(new GuiFactoryPlacer());
    GuiHandler.registerGui(new GuiFactoryActivator());
    GuiHandler.registerGui(new GuiFactoryFluidIntake());
    GuiHandler.registerGui(new GuiFactoryFluidPlacer());
    GuiHandler.registerGui(new GuiFactorySuperBreaker());
    GuiHandler.registerGui(new GuiFactorySuperPlacer());
    GuiHandler.registerGui(new GuiFactorySuperActivator());
    GuiHandler.registerGui(new GuiFactorySprinkler());
    GuiHandler.registerGui(new GuiFactoryRangedCollector());
    GuiHandler.registerGui(new GuiFactoryOilWell());
    GuiHandler.registerGui(new GuiFactoryCombustionGen());
    GuiHandler.registerGui(new GuiFactoryDistiller());
    GuiHandler.registerGui(new GuiFactoryDrill());
  }
}
