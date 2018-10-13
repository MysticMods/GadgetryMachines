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
import epicsquid.gadgetry.machines.util.DummyNetHandlerPlayServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TileActivator extends TileModular implements ITickable {
  public static final String INVENTORY = "inventory";
  FakePlayer player;
  public float light = 0;
  public int ticks = 0;
  public int offset = 0;

  public TileActivator() {
    addModule(new ModuleInventory(INVENTORY, this, 1, "block_activator", new int[] { 0 }, new int[] { 0 }).setSlotPredicate(0, new PredicateTrue()));
    config.setAllIO(FaceIO.IN);
    config.setAllModules(INVENTORY);
  }

  @Override
  public void update() {
    if (!world.isRemote) {
      if (player == null) {
        player = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "ultima_activator"));
        new DummyNetHandlerPlayServer(player);
      }
      ticks++;
      if (ticks >= 20) {
        ticks = 0;
        EnumFacing blockFace = world.getBlockState(getPos()).getValue(BlockTEFacing.facing);
        ModuleInventory moduleInv = (ModuleInventory) this.modules.get(INVENTORY);
        IInventory inv = ((IInventory) moduleInv);
        BlockPos p = getPos().offset(blockFace);
        IBlockState state = world.getBlockState(p);
        boolean stop = false;
        for (int i = 0; i < 4 && state.getBlock() == Blocks.AIR; i++) {
          p = p.offset(blockFace);
          state = world.getBlockState(p);
        }
        ItemStack stack = inv.getStackInSlot(0);
        Vec3i fDir = blockFace.getDirectionVec();
        player.posX = getPos().getX() + 0.5 + fDir.getX();
        player.posY = getPos().getY() + 0.5 + fDir.getY() - player.getEyeHeight();
        player.posZ = getPos().getZ() + 0.5 + fDir.getZ();
        player.rotationYaw = blockFace.getHorizontalAngle();
        player.rotationYawHead = blockFace.getHorizontalAngle();
        player.rotationPitch = fDir.getY() * -90f;
        player.setHeldItem(EnumHand.MAIN_HAND, stack);
        stack.getItem().onItemRightClick(world, player, EnumHand.MAIN_HAND);
        AxisAlignedBB box = new AxisAlignedBB(getPos().getX() - 1 + fDir.getX() * 2.0f, getPos().getY() - 1 + fDir.getY() * 2.0f,
            getPos().getZ() - 1 + fDir.getZ() * 2.0f, getPos().getX() + 2 + fDir.getX() * 2.0f, getPos().getY() + 2 + fDir.getY() * 2.0f,
            getPos().getZ() + 2 + fDir.getZ() * 2.0f);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        EnumActionResult result = EnumActionResult.PASS;
        boolean stop2 = false;
        if (entities.size() > 0) {
          if (!stop) {
            stack.getItem().itemInteractionForEntity(stack, player, entities.get(offset % entities.size()), EnumHand.MAIN_HAND);
            stop2 = entities.get(offset % entities.size()).processInitialInteract(player, EnumHand.MAIN_HAND);
          }
          if (result != EnumActionResult.SUCCESS) {
            result = entities.get(offset % entities.size()).applyPlayerInteraction(player, player.getLookVec(), EnumHand.MAIN_HAND);
          }
          offset++;
        }
        if (state.getBlock() != Blocks.AIR) {
          stack.getItem().onItemUse(player, world, p, EnumHand.MAIN_HAND, blockFace.getOpposite(), 0.5f * Math.abs(fDir.getX()), 0.5f * Math.abs(fDir.getY()),
              0.5f * Math.abs(fDir.getZ()));
          state.getBlock().onBlockActivated(world, p, state, player, EnumHand.MAIN_HAND, blockFace.getOpposite(), 0.5f * Math.abs(fDir.getX()),
              0.5f * Math.abs(fDir.getY()), 0.5f * Math.abs(fDir.getZ()));
        }
        inv.setInventorySlotContents(0, player.getHeldItem(EnumHand.MAIN_HAND));
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
