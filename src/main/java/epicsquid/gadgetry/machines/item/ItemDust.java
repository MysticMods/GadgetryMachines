package epicsquid.gadgetry.machines.item;

import epicsquid.gadgetry.core.lib.item.ItemBase;
import epicsquid.gadgetry.machines.GadgetryMachines;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public class ItemDust extends ItemBase {
  int baseColor = 0;
  int lightColor = 0;
  int glimmerColor = 0;

  public ItemDust(String name, int base, int lighter, int glimmer) {
    super(name);
    this.baseColor = base;
    this.lightColor = lighter;
    this.glimmerColor = glimmer;
  }

  public class DustColorHandler implements IItemColor {
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
      switch (tintIndex) {
      case 0:
        return baseColor;
      case 1:
        return lightColor;
      case 2:
        return glimmerColor;
      default:
        return 0xFFFFFF;
      }
    }
  }

  @Override
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(GadgetryMachines.MODID + ":dust", "inventory"));
  }
}
