package epicsquid.gadgetry.machines.tile;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import epicsquid.gadgetry.core.block.BlockTEFacing;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.util.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileBreaker extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  FakePlayer player;
  public float light = 0;
  public int ticks = 0;

  public TileBreaker() {
    addModule(new ModuleInventory(INVENTORY, this, 9, "block_breaker", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 })
        .setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue()));
    config.setAllIO(FaceIO.OUT);
    config.setAllModules(INVENTORY);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      if (player == null) {
        player = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "ultima_breaker"));
      }
      ticks++;
      if (ticks >= 20) {
        ticks = 0;
        ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
        IInventory inv = ((IInventory) moduleInv);
        BlockPos p = getPos().offset(world.getBlockState(getPos()).getValue(BlockTEFacing.facing));
        IBlockState state = world.getBlockState(p);
        if (state.getBlockHardness(world, p) > 0 && state.getBlockHardness(world, p) < 20f && state.getBlock().getHarvestLevel(state) < 3) {
          state.getBlock().onBlockHarvested(world, p, state, player);
          List<ItemStack> items = state.getBlock().getDrops(world, p, state, 0);
          for (ItemStack i : items) {
            int inserted = InventoryUtil.attemptInsert(i.copy(), moduleInv.inventory, false);
            i.shrink(inserted);
            if (i.getCount() > 0) {
              world.spawnEntity(new EntityItem(world, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, i));
            }
          }
          world.destroyBlock(p, false);
          world.notifyBlockUpdate(p, state, Blocks.AIR.getDefaultState(), 8);
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
