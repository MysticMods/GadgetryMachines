package epicsquid.gadgetry.machines.tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import epicsquid.gadgetry.core.lib.container.ContainerModular;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateEmpty;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.recipe.RecipeBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileAssemblyPress extends TileModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String INVENTORY = "inventory";
  public float angle = -1;
  public int[] progress = { 0, 40 };

  public TileAssemblyPress() {
    addModule(new ModuleInventory(INVENTORY, this, 20, "assembly_press", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 9 }) {
      @Override
      public boolean canInsertToSlot(int slot) {
        return slot != 9;
      }

      @Override
      public boolean canExtractFromSlot(int slot) {
        return slot != 19;
      }
    }.setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue())
        .setSlotPredicate(9, new PredicateEmpty()).setSlotPredicate(10, new PredicateTrue()).setSlotPredicate(11, new PredicateTrue())
        .setSlotPredicate(12, new PredicateTrue()).setSlotPredicate(13, new PredicateTrue()).setSlotPredicate(14, new PredicateTrue())
        .setSlotPredicate(15, new PredicateTrue()).setSlotPredicate(16, new PredicateTrue()).setSlotPredicate(17, new PredicateTrue())
        .setSlotPredicate(18, new PredicateTrue()).setSlotPredicate(19, new PredicateEmpty()));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.IN);
  }

  public static CountComparator countComparator = new CountComparator();

  public static class CountComparator implements Comparator<ItemStack> {
    @Override
    public int compare(ItemStack arg0, ItemStack arg1) {
      return Integer.compare(arg0.getCount(), arg1.getCount());
    }
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      IInventory inventory = (IInventory) modules.get(INVENTORY);
      ModuleEnergy energy = (ModuleEnergy) modules.get(BATTERY);
      InventoryCrafting inv = new InventoryCrafting(new ContainerModular(this), 3, 3);
      for (int i = 0; i < 9; i++) {
        inv.setInventorySlotContents(i, inventory.getStackInSlot(i + 10));
      }
      IRecipe recipe = CraftingManager.findMatchingRecipe(inv, world);
      if (recipe == null && !inventory.getStackInSlot(19).isEmpty()) {
        inventory.setInventorySlotContents(19, ItemStack.EMPTY);
        markDirty();
      } else if (recipe != null && recipe.matches(inv, world) && !RecipeBase.stackMatches(recipe.getRecipeOutput(), inventory.getStackInSlot(19))) {
        inventory.setInventorySlotContents(19, recipe.getRecipeOutput());
        markDirty();
      }

      boolean doContinue = true;
      for (int i = 0; i < 9; i++) {
        ItemStack s = inventory.getStackInSlot(i);
        if (!s.isEmpty()) {
          List<ItemStack> validInserts = new ArrayList<ItemStack>();
          for (int j = 0; j < 9; j++) {
            if (RecipeBase.stackMatches(s, inventory.getStackInSlot(j + 10))) {
              validInserts.add(inventory.getStackInSlot(j + 10));
            }
          }
          validInserts.sort(countComparator);
          if (validInserts.size() > 0 && validInserts.get(0).getCount() < validInserts.get(0).getMaxStackSize()) {
            validInserts.get(0).grow(1);
            s.shrink(1);
            if (s.getCount() <= 0) {
              inventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
            markDirty();
            doContinue = false;
          }
        }
      }
      if (recipe != null && recipe.matches(inv, world) && energy.battery.getEnergyStored() >= 10 && (inventory.getStackInSlot(9).isEmpty()
          || RecipeBase.stackMatches(inventory.getStackInSlot(9), recipe.getRecipeOutput())
          && inventory.getStackInSlot(9).getCount() + recipe.getRecipeOutput().getCount() <= inventory.getStackInSlot(9).getMaxStackSize())) {
        boolean hasEnoughItems = true;
        for (int i = 10; i < 19; i++) {
          if (!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).getCount() <= 1) {
            hasEnoughItems = false;
          }
        }
        if (hasEnoughItems) {
          energy.battery.extractEnergy(10, false);
          progress[0]++;
          if (progress[0] > progress[1]) {
            progress[0] = 0;
            ItemStack stack = recipe.getCraftingResult(inv);
            if (RecipeBase.stackMatches(stack, inventory.getStackInSlot(9))) {
              inventory.getStackInSlot(9).grow(stack.getCount());
            } else if (inventory.getStackInSlot(9).isEmpty()) {
              inventory.setInventorySlotContents(9, stack);
            } else {
              world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack));
            }
            NonNullList<ItemStack> remaining = recipe.getRemainingItems(inv);
            for (int i = 0; i < 9; i++) {
              inventory.getStackInSlot(i + 10).shrink(1);
              if (inventory.getStackInSlot(i + 10).getCount() <= 0) {
                inventory.setInventorySlotContents(i + 10, ItemStack.EMPTY);
              }
            }
            for (int i = 0; i < remaining.size(); i++) {
              if (!remaining.get(i).isEmpty()) {
                inventory.setInventorySlotContents(i + 10, remaining.get(i));
              }
            }
          }
          markDirty();
        }
      }
    }

    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    IInventory inv = ((IInventory) modules.get(INVENTORY));
    inv.setInventorySlotContents(19, ItemStack.EMPTY);
    markDirty();
    super.breakBlock(world, pos, state, player);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setInteger("progress", progress[0]);
    tag.setInteger("maxProgress", progress[1]);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    progress[0] = tag.getInteger("progress");
    progress[1] = tag.getInteger("maxProgress");
  }
}
