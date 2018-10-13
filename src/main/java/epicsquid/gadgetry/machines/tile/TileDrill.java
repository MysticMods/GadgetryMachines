package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.block.BlockTEMultiHoriz;
import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.fluid.predicate.PredicateWhitelist;
import epicsquid.gadgetry.core.lib.inventory.InventoryHandler;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.lib.tile.module.ModuleInventory;
import epicsquid.gadgetry.core.lib.util.InventoryUtil;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import epicsquid.gadgetry.core.tile.TileMultiModular;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import epicsquid.gadgetry.machines.block.BlockDrill;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileDrill extends TileMultiModular implements ITickable {

  public static final String BATTERY = "battery";
  public static final String TANK = "tank";
  public static final String INVENTORY = "inventory";
  public float angle = -1;
  public int distance = 2;
  public EnumFacing face = EnumFacing.UP;
  public int[] progress = new int[] { 0, 4 };
  public int anim = 0;
  public boolean active = false;
  public BlockPos next = new BlockPos(0, 0, 0);

  public TileDrill() {
    addModule(
        new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(16000, new PredicateWhitelist(new Fluid[] { GadgetryMachinesContent.fuel }), false)));
    addModule(new ModuleEnergy(BATTERY, this, 640000, 6400, 6400));
    addModule(new ModuleInventory(INVENTORY, this, 9, "drill", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 })
        .setSlotPredicate(0, new PredicateTrue()).setSlotPredicate(1, new PredicateTrue()).setSlotPredicate(2, new PredicateTrue())
        .setSlotPredicate(3, new PredicateTrue()).setSlotPredicate(4, new PredicateTrue()).setSlotPredicate(5, new PredicateTrue())
        .setSlotPredicate(6, new PredicateTrue()).setSlotPredicate(7, new PredicateTrue()).setSlotPredicate(8, new PredicateTrue()));
    config.setAllIO(FaceIO.IN);
  }

  @Override
  public void update() {
    if (face == EnumFacing.UP) {
      face = world.getBlockState(pos).getValue(BlockTEMultiHoriz.facing);
    }
    ModuleEnergy battery = ((ModuleEnergy) modules.get(BATTERY));
    ExtendedFluidTank tank = ((ModuleFluid) modules.get(TANK)).tanks.get(0);
    InventoryHandler inv = ((ModuleInventory) modules.get(INVENTORY)).inventory;
    if (!world.isRemote) {
      active = false;
      if (battery.battery.getEnergyStored() >= 40 && tank.getFluidAmount() >= 1) {
        for (int r = 0; r < 6 && (next.getY() == 0 || world.isAirBlock(next)); r++) {
          for (int i = -r; i < r + 1 && (next.getY() == 0 || world.isAirBlock(next)); i++) {
            for (int j = -r; j < r + 1 && (next.getY() == 0 || world.isAirBlock(next)); j++) {
              BlockPos p = pos.offset(face, -distance).offset(face.rotateY(), i).up(j + 1);
              IBlockState s = world.getBlockState(p);
              if (!(s instanceof BlockDrill) && s.getBlockHardness(world, p) > 0 && s.getBlockHardness(world, p) < 20f) {
                next = p;
              }
            }
          }
        }
        if (next.getY() > 0) {
          IBlockState s = world.getBlockState(next);
          NonNullList<ItemStack> list = NonNullList.create();
          s.getBlock().getDrops(list, world, next, s, 0);
          boolean canBreak = (!(s instanceof BlockDrill) && s.getBlockHardness(world, next) > 0 && s.getBlockHardness(world, next) < 20f);
          if (list.size() > 0 && canBreak) {
            for (int i = 0; i < list.size(); i++) {
              ItemStack stack = list.get(i).copy();
              if (InventoryUtil.attemptInsert(stack, inv, true) == 0) {
                canBreak = false;
              }
            }
          }
          if (canBreak) {
            active = true;
            battery.battery.extractEnergy(40, false);
            tank.drain(1, true);
            progress[0]++;
            anim++;
            if (progress[0] >= progress[1]) {
              world.destroyBlock(next, false);
              for (int i = 0; i < list.size(); i++) {
                ItemStack stack = list.get(i).copy();
                InventoryUtil.attemptInsert(stack, inv, false);
              }
              next = new BlockPos(0, 0, 0);
              progress[0] = 0;
            }
          }
        } else if (distance < 96) {
          active = true;
          battery.battery.extractEnergy(40, false);
          tank.drain(1, true);
          progress[0]++;
          anim++;
          if (progress[0] >= progress[1]) {
            progress[0] = 0;
            distance++;
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
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return super.shouldRefresh(world, pos, oldState, newState) && newState.getBlock() != GadgetryMachinesContent.combustion_gen;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    tag.setInteger("face", face.ordinal());
    tag.setInteger("dist", distance);
    tag.setInteger("progress", progress[0]);
    tag.setInteger("anim", anim);
    tag.setBoolean("active", active);
    tag.setLong("next", next.toLong());
    return tag;
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    face = face.VALUES[tag.getInteger("face")];
    distance = tag.getInteger("dist");
    progress[0] = tag.getInteger("progress");
    anim = tag.getInteger("anim");
    active = tag.getBoolean("active");
    next = BlockPos.fromLong(tag.getLong("next"));
  }

  @SideOnly(Side.CLIENT)
  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return this.INFINITE_EXTENT_AABB;
  }
}
