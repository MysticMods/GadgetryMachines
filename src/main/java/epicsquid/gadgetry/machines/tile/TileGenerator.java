package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEOnOffHoriz;
import epicsquid.gadgetry.core.lib.inventory.predicates.PredicateFurnaceFuel;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.util.Util;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileGenerator extends TileModular implements ITickable {

  public static final String INVENTORY = "inventory";
  public static final String BATTERY = "battery";

  public int[] ticks = { 0, 0 };

  public TileGenerator() {
    addModule(new ModuleInventory(INVENTORY, this, 1, "furnace_gen", new int[] { 0 }, new int[] {}).setSlotPredicate(0, new PredicateFurnaceFuel()));
    addModule(new ModuleEnergy(BATTERY, this, 160000, 1600, 1600));
    config.setAllIO(FaceIO.OUT);
    config.setAllModules(BATTERY);
  }

  @Override
  public void update() {
    IBlockState state = world.getBlockState(getPos());
    if (!world.isRemote) {
      ModuleEnergy battery = (ModuleEnergy) modules.get(BATTERY);
      if (ticks[0] == 0 && battery.battery.receiveEnergy(1, true) > 0) {
        IInventory inv = (IInventory) modules.get(INVENTORY);
        int burnTime = TileEntityFurnace.getItemBurnTime(inv.getStackInSlot(0));
        if (burnTime > 0) {
          inv.decrStackSize(0, 1);
          ticks[0] = burnTime;
          ticks[1] = burnTime;
          world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, true), 8);
          world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, true), 8);
          markDirty();
        }
      } else {
        battery.battery.receiveEnergy(40, false);
        ticks[0]--;

        if (ticks[0] <= 0 && battery.battery.receiveEnergy(1, true) > 0) {
          IInventory inv = (IInventory) modules.get(INVENTORY);
          int burnTime = TileEntityFurnace.getItemBurnTime(inv.getStackInSlot(0));
          if (burnTime > 0) {
            inv.decrStackSize(0, 1);
            ticks[0] = Math.max(ticks[0], burnTime);
            ticks[1] = Math.max(ticks[0], burnTime);
            markDirty();
          }
        }
        if (ticks[0] > 0 && ticks[0] % 20 == 0) {
          if (!world.getBlockState(getPos()).getValue(BlockTEOnOffHoriz.active)) {
            world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, true), 8);
            world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, true), 8);
          }
        }
        if (ticks[0] <= 0) {
          world.setBlockState(getPos(), state.withProperty(BlockTEOnOffHoriz.active, false), 8);
          world.notifyBlockUpdate(getPos(), state, state.withProperty(BlockTEOnOffHoriz.active, false), 8);
        }
        markDirty();
      }
    }
    if (ticks[0] > 0 && Util.rand.nextInt(14) == 0) {
      spawnParticle(state);
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  public void spawnParticle(IBlockState state) {
    EnumFacing enumfacing = (EnumFacing) state.getValue(BlockTEOnOffHoriz.facing);
    double d0 = (double) pos.getX() + 0.5;
    double d1 = (double) pos.getY() + 0.5 + Util.rand.nextDouble() * 6.0 / 16.0;
    double d2 = (double) pos.getZ() + 0.5;
    double d3 = 0.52;
    double d4 = Util.rand.nextDouble() * 0.6D - 0.3D;

    if (Util.rand.nextDouble() < 0.1D) {
      world.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS,
          1.0F, 1.0F, false);
    }

    switch (enumfacing) {
    case WEST:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      break;
    case EAST:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      break;
    case NORTH:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
      break;
    case SOUTH:
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
      world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return newState.getBlock() != GadgetryMachinesContent.furnace_gen;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    tag.setInteger("ticks", ticks[0]);
    tag.setInteger("lastFuel", ticks[1]);
    return super.writeToNBT(tag);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    ticks[0] = tag.getInteger("ticks");
    ticks[1] = tag.getInteger("lastFuel");
  }
}
