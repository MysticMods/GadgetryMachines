package epicsquid.gadgetry.machines.tile;

import java.util.List;

import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import epicsquid.gadgetry.core.util.InventoryUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileRangedCollector extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  public int ticks = 0;

  public TileRangedCollector() {
    addModule(new ModuleInventory(INVENTORY, this, 13, "ranged_collector", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }) {
      @Override
      public boolean isItemValidForSlot(int index, ItemStack stack) {
        boolean filtered = false;
        if (index >= 0 && index < 9) {
          filtered = false;
          for (int i = 9; i < 13; i++) {
            if (!((IInventory) this).getStackInSlot(i).isEmpty()) {
              filtered = true;
            }
          }
          if (filtered) {
            for (int i = 9; i < 13; i++) {
              ItemStack s = ((IInventory) this).getStackInSlot(i);
              if (RecipeBase.stackMatches(stack, s)) {
                filtered = false;
              }
            }
          }
        }
        return predicates.get(index).test(stack) && !filtered;
      }
    }.setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue())
        .setSlotPredicate(9, new PredicateTrue()).setSlotPredicate(10, new PredicateTrue()).setSlotPredicate(11, new PredicateTrue())
        .setSlotPredicate(12, new PredicateTrue()));
    config.setAllIO(FaceIO.OUT);
    config.setAllModules(INVENTORY);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      ticks++;
      if (ticks >= 20) {
        ticks = 0;
        ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
        IInventory inv = ((IInventory) moduleInv);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
            new AxisAlignedBB(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 5, pos.getY() + 2, pos.getZ() + 5));
        for (EntityItem i : items) {
          if (i.onGround && Math.round(20f * i.motionX) == 0 && Math.round(20f * i.motionY) == 0 && Math.round(20f * i.motionZ) == 0) {
            ItemStack s = i.getItem().copy();
            int inserted = InventoryUtil.attemptInsert(s, moduleInv.inventory, false, 0, 9);
            s.shrink(inserted);
            i.setItem(s);
            if (s.isEmpty()) {
              i.setDead();
            }
          }
        }
        markDirty();
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
  }
}
