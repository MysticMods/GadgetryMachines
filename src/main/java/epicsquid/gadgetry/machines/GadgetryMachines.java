package epicsquid.gadgetry.machines;

import epicsquid.gadgetry.core.GadgetryCore;
import epicsquid.gadgetry.machines.proxy.CommonProxy;
import epicsquid.gadgetry.machines.recipe.MachinesRecipeRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = GadgetryMachines.MODID, version = GadgetryMachines.VERSION, name = GadgetryMachines.NAME, dependencies = GadgetryMachines.DEPENDENCIES)
public class GadgetryMachines {
  public static final String MODID = "gadgetrymachines";
  public static final String VERSION = "@VERSION@";
  public static final String NAME = "Gadgetry: Machines";
  public static final String DEPENDENCIES = "required-before:gadgetrycore@[" + GadgetryCore.VERSION + ",)";

  public static ModContainer CONTAINER;

  @SidedProxy(clientSide = "epicsquid.gadgetry.machines.proxy.ClientProxy", serverSide = "epicsquid.gadgetry.machines.proxy.CommonProxy") public static CommonProxy proxy;

  @Instance public static GadgetryMachines INSTANCE;

  static {
    FluidRegistry.enableUniversalBucket();
  }

  public static CreativeTabs tab = new CreativeTabs("gadgetrymachines") {
    @Override
    public String getTabLabel() {
      return "gadgetrymachines";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
      return new ItemStack(GadgetryMachinesContent.furnace_gen, 1);
    }
  };

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    CONTAINER = Loader.instance().activeModContainer();
    MinecraftForge.EVENT_BUS.register(new GadgetryMachinesContent());
    MinecraftForge.EVENT_BUS.register(new MachinesRecipeRegistry());
    proxy.preInit(event);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init(event);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit(event);
  }
}
