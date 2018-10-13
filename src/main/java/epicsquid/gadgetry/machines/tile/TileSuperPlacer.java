package epicsquid.gadgetry.machines.tile;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.machines.inventory.predicates.PredicateBlockFilter;
import epicsquid.gadgetry.machines.item.IBlockFilter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileSuperPlacer extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  public static final String BATTERY = "battery";
  FakePlayer player;
  public float light = 0;
  public int ticks = 0;

  public TileSuperPlacer() {
    super();
    addModule(new ModuleEnergy(BATTERY, this, 80000, 1600, 1600));
    addModule(new ModuleInventory(INVENTORY, this, 13, "super_placer", new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12 }, new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12 })
        .setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue()).setSlotPredicate(6, new PredicateTrue())
        .setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue()).setSlotPredicate(9, new PredicateTrue())
        .setSlotPredicate(10, new PredicateTrue()).setSlotPredicate(11, new PredicateTrue()).setSlotPredicate(12, new PredicateTrue())
        .setSlotPredicate(0, new PredicateBlockFilter()).setSlotPredicate(1, new PredicateBlockFilter()).setSlotPredicate(2, new PredicateBlockFilter())
        .setSlotPredicate(3, new PredicateBlockFilter()));
    config.setAllIO(FaceIO.IN);
    config.setAllModules(INVENTORY);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      if (player == null) {
        player = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "ultima_placer"));
      }
      ticks++;
      ModuleEnergy battery = (ModuleEnergy) this.modules.get(BATTERY);
      if (ticks >= 20 && battery.battery.getEnergyStored() > 200) {
        markDirty();
        ticks = 0;
        EnumFacing blockFace = world.getBlockState(getPos()).getValue(BlockTEFacing.facing);
        ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
        IInventory inv = ((IInventory) moduleInv);
        boolean hasPlaced = false;
        for (int i = 0; i < 9 && !hasPlaced; i++) {
          BlockPos p = getPos().offset(blockFace, i + 1);
          IBlockState state = world.getBlockState(p);
          IBlockState downState = world.getBlockState(p.down());
          boolean doPlace = false;
          if (inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(3).isEmpty()) {
            doPlace = true;
          } else
            for (int j = 0; j < 4; j++) {
              ItemStack stack = inv.getStackInSlot(j);
              if (stack.getItem() instanceof IBlockFilter) {
                doPlace = doPlace || ((IBlockFilter) stack.getItem()).matchesBlock(world, p.down(), stack, downState);
              }
            }
          if (doPlace && (state.getBlock() == Blocks.AIR || state.getBlock().isReplaceable(world, p))) {
            boolean doContinue = true;
            for (int j = 0; j < inv.getSizeInventory() && doContinue; j++) {
              ItemStack stack = inv.getStackInSlot(j);
              if (stack.getItem() instanceof ItemBlock) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
                Block b = Block.getBlockFromItem(stack.getItem());
                BlockPos p2 = p.offset(blockFace, 1);
                EnumFacing placeFace = EnumFacing.UP;
                if (world.mayPlace(b, p, true, placeFace, player)) {
                  IBlockState placeState = b.getStateForPlacement(world, p, placeFace, 0.75f, 0.75f, 0.75f, stack.getItemDamage(), player, EnumHand.MAIN_HAND);
                  EnumActionResult result = ((ItemBlock) stack.getItem()).onItemUse(player, world, p, EnumHand.MAIN_HAND, placeFace, 0f, 0f, 0f);
                  if (result == EnumActionResult.SUCCESS) {
                    world.notifyBlockUpdate(p, state, placeState, 8);
                    if (stack.getCount() == 0) {
                      inv.setInventorySlotContents(j, ItemStack.EMPTY);
                    }
                    battery.battery.extractEnergy(200, false);
                    doContinue = false;
                    hasPlaced = true;
                  }
                  markDirty();
                }
              } else if (stack.getItem() instanceof IPlantable) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
                Block b = Block.getBlockFromItem(stack.getItem());
                BlockPos p2 = getPos().offset(blockFace, 2);
                EnumFacing placeFace = EnumFacing.UP;
                IPlantable s = (IPlantable) stack.getItem();
                IBlockState ground = world.getBlockState(p.down());
                if (ground.getBlock().canSustainPlant(ground, world, p.down(), placeFace, s)) {
                  IBlockState placeState = s.getPlant(world, p);
                  EnumActionResult result = stack.getItem().onItemUse(player, world, p.down(), EnumHand.MAIN_HAND, placeFace, 0f, 0f, 0f);
                  if (result == EnumActionResult.SUCCESS) {
                    world.notifyBlockUpdate(p, state, placeState, 8);
                    if (stack.getCount() == 0) {
                      inv.setInventorySlotContents(j, ItemStack.EMPTY);
                    }
                    battery.battery.extractEnergy(200, false);
                    doContinue = false;
                    hasPlaced = true;
                  }
                  markDirty();
                }
              } else if (stack.getItem() instanceof ItemBlockSpecial) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
                Block b = Block.getBlockFromItem(stack.getItem());
                BlockPos p2 = getPos().offset(blockFace, 2);
                EnumFacing placeFace = EnumFacing.UP;
                if (!world.isAirBlock(p2)) {
                  placeFace = blockFace.getOpposite();
                }
                if (world.mayPlace(b, p, true, placeFace, player)) {
                  IBlockState placeState = b.getStateForPlacement(world, p, placeFace, 0.75f, 0.75f, 0.75f, stack.getItemDamage(), player, EnumHand.MAIN_HAND);
                  EnumActionResult result = ((ItemBlockSpecial) stack.getItem()).onItemUse(player, world, p, EnumHand.MAIN_HAND, placeFace, 0f, 0f, 0f);
                  if (result == EnumActionResult.SUCCESS) {
                    world.notifyBlockUpdate(p, state, placeState, 8);
                    if (stack.getCount() == 0) {
                      inv.setInventorySlotContents(j, ItemStack.EMPTY);
                    }
                    battery.battery.extractEnergy(200, false);
                    doContinue = false;
                    hasPlaced = true;
                  }
                  markDirty();
                }
              }
            }
          }
        }
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
