package epicsquid.gadgetry.machines.tile;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
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

public class TilePlacer extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  FakePlayer player;
  public float light = 0;
  public int ticks = 0;

  public TilePlacer() {
    addModule(new ModuleInventory(INVENTORY, this, 9, "block_placer", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 })
        .setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue()));
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
      if (ticks >= 20) {
        ticks = 0;
        EnumFacing blockFace = world.getBlockState(getPos()).getValue(BlockTEFacing.facing);
        ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
        IInventory inv = ((IInventory) moduleInv);
        BlockPos p = getPos().offset(blockFace);
        IBlockState state = world.getBlockState(p);
        if (state.getBlock() == Blocks.AIR || state.getBlock().isReplaceable(world, p)) {
          boolean doContinue = true;
          for (int i = 0; i < inv.getSizeInventory() && doContinue; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock) {
              player.setHeldItem(EnumHand.MAIN_HAND, stack);
              Block b = Block.getBlockFromItem(stack.getItem());
              BlockPos p2 = getPos().offset(blockFace, 2);
              EnumFacing placeFace = EnumFacing.UP;
              if (!world.isAirBlock(p2)) {
                placeFace = blockFace.getOpposite();
              }
              if (world.mayPlace(b, p, true, placeFace, player)) {
                IBlockState placeState = b.getStateForPlacement(world, p, placeFace, 0.75f, 0.75f, 0.75f, stack.getItemDamage(), player, EnumHand.MAIN_HAND);
                EnumActionResult result = ((ItemBlock) stack.getItem()).onItemUse(player, world, p, EnumHand.MAIN_HAND, placeFace, 0f, 0f, 0f);
                if (result == EnumActionResult.SUCCESS) {
                  world.notifyBlockUpdate(p, state, placeState, 8);
                  if (stack.getCount() == 0) {
                    inv.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  doContinue = false;
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
                    inv.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  doContinue = false;
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
                    inv.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  doContinue = false;
                }
                markDirty();
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
