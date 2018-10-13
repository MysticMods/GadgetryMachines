package epicsquid.gadgetry.machines.tile;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.util.InventoryUtil;
import epicsquid.gadgetry.machines.inventory.predicates.PredicateBlockFilter;
import epicsquid.gadgetry.machines.item.IBlockFilter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileSuperBreaker extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  public static final String BATTERY = "battery";
  FakePlayer player;
  public float light = 0;
  public int ticks = 0;

  public TileSuperBreaker() {
    super();
    addModule(new ModuleEnergy(BATTERY, this, 80000, 1600, 1600));
    addModule(new ModuleInventory(INVENTORY, this, 13, "super_breaker", new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12 }, new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12 })
        .setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue()).setSlotPredicate(6, new PredicateTrue())
        .setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue()).setSlotPredicate(9, new PredicateTrue())
        .setSlotPredicate(10, new PredicateTrue()).setSlotPredicate(11, new PredicateTrue()).setSlotPredicate(12, new PredicateTrue())
        .setSlotPredicate(0, new PredicateBlockFilter()).setSlotPredicate(1, new PredicateBlockFilter()).setSlotPredicate(2, new PredicateBlockFilter())
        .setSlotPredicate(3, new PredicateBlockFilter()));
    config.setAllIO(FaceIO.OUT);
    config.setAllModules(INVENTORY);
    this.validIOModules.remove(BATTERY);
  }

  public boolean isSelectable(AxisAlignedBB box) {
    return box.maxY - box.minY > 0 && box.maxX - box.minX > 0 && box.maxZ - box.minZ > 0;
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      if (player == null) {
        player = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "ultima_super_breaker"));
      }
      ticks++;
      ModuleEnergy battery = (ModuleEnergy) this.modules.get(BATTERY);
      if (ticks >= 20 && battery.battery.getEnergyStored() > 200) {
        markDirty();
        ticks = 0;
        for (int i = 0; i < 9; i++) {
          ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
          IInventory inv = ((IInventory) moduleInv);
          BlockPos p = getPos().offset(world.getBlockState(getPos()).getValue(BlockTEFacing.facing), i + 1);
          IBlockState state = world.getBlockState(p);
          boolean doDestroy = false;
          if (inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(3).isEmpty()) {
            doDestroy = true;
          } else
            for (int j = 0; j < 4; j++) {
              ItemStack stack = inv.getStackInSlot(j);
              if (stack.getItem() instanceof IBlockFilter) {
                doDestroy = doDestroy || ((IBlockFilter) stack.getItem()).matchesBlock(world, p, stack, state);
              }
            }
          if (doDestroy && state.getBlock() != Blocks.AIR && isSelectable(state.getBoundingBox(world, p)) && state.getBlockHardness(world, p) >= 0
              && state.getBlockHardness(world, p) < 20f && state.getBlock().getHarvestLevel(state) < 3) {
            battery.battery.extractEnergy(200, false);
            state.getBlock().onBlockHarvested(world, p, state, player);
            List<ItemStack> items = state.getBlock().getDrops(world, p, state, 0);
            for (ItemStack s : items) {
              int inserted = InventoryUtil.attemptInsert(s.copy(), moduleInv.inventory, false);
              s.shrink(inserted);
              if (s.getCount() > 0) {
                world.spawnEntity(new EntityItem(world, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, s));
              }
            }
            world.destroyBlock(p, false);
            world.notifyBlockUpdate(p, state, Blocks.AIR.getDefaultState(), 8);
            break;
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
