package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.fluid.predicate.PredicateWhitelist;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleEnergy;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.lib.util.Util;
import epicsquid.gadgetry.machines.block.BlockSprinkler;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleWaterWake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class TileSprinkler extends TileModular implements ITickable {

  public static final String TANK = "tank";
  public static final String BATTERY = "battery";
  public static int progress = 0;
  public static int lifetime = 0;

  public TileSprinkler() {
    addModule(new ModuleFluid(TANK, this, 125).addTank(new ExtendedFluidTank(8000, new PredicateWhitelist(FluidRegistry.WATER), true)));
    addModule(new ModuleEnergy(BATTERY, this, 80000, 1600, 1600));
    config.setAllIO(FaceIO.IN);
    config.setAllModules(TANK);
  }

  @Override
  public void update() {
    lifetime++;
    ModuleFluid fluids = (ModuleFluid) modules.get(TANK);
    ModuleEnergy battery = (ModuleEnergy) modules.get(BATTERY);
    ExtendedFluidTank tank = fluids.tanks.get(0);
    boolean aboveWater = world.getBlockState(pos.down()).getBlock() == Blocks.WATER;
    if ((tank.getFluidAmount() > 40) && battery.battery.getEnergyStored() > 200) {
      if (!world.isRemote) {
        progress++;
        if (progress >= 20) {
          progress = 0;
          if (!aboveWater) {
            FluidStack toDrain = tank.getFluid().copy();
            toDrain.amount = 40;
            tank.drain(toDrain, true);
          }
          battery.battery.extractEnergy(200, false);
          markDirty();
          for (int i = -4; i < 5; i++) {
            for (int j = -4; j < 5; j++) {
              if (Util.rand.nextInt(8) == 0) {
                for (int k = -1; k < 2; k++) {
                  BlockPos p = pos.add(i, k, j);
                  IBlockState state = world.getBlockState(p);
                  if (state.getBlock() instanceof IGrowable
                      || (state.getBlock().getTickRandomly() && state.getMaterial() == Material.PLANTS) && Util.rand.nextInt(6) == 0) {
                    state.getBlock().randomTick(world, p, state, world.rand);
                  }
                }
              }
            }
          }
        }
      } else {
        IBlockState state = world.getBlockState(getPos());
        if (state.getBlock() instanceof BlockSprinkler) {
          EnumFacing face = state.getValue(BlockSprinkler.facing);
          Vec3i dir = face.getDirectionVec();
          float baseX = getPos().getX() + 0.5f - (4.5f / 16f) * dir.getZ();
          float baseY = getPos().getY() + 0.5f;
          float baseZ = getPos().getZ() + 0.5f - (4.5f / 16f) * dir.getX();
          float sin = MathHelper.sin(0.001f * (lifetime + Minecraft.getMinecraft().getRenderPartialTicks()));
          for (int i = 0; i < 4; i++) {
            ParticleWaterWake p = new ParticleWaterWake(world, baseX + (9f / 16f) * (i / 3f) * dir.getZ(), baseY, baseZ + (9f / 16f) * (i / 3f) * dir.getX(),
                sin * 0.25 * dir.getX() + (-0.1f + 0.2f * (i / 3f)) * dir.getZ(), 0.6, sin * 0.25 * dir.getZ() + (-0.1f + 0.2f * (i / 3f)) * dir.getX()) {
              @Override
              public void onUpdate() {
                if (this.particleAge == 0) {
                  particleGravity = 0.05f;
                  particleMaxAge = 40;
                }
                this.particleAge++;
                super.onUpdate();
              }
            };
            Minecraft.getMinecraft().effectRenderer.addEffect(p);
          }
        }
      }
    }
    for (Module m : modules.values()) {
      m.onUpdate(this);
    }
  }

  @Override
  public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
      float hitZ) {
    ItemStack heldItem = player.getHeldItem(hand);
    if (heldItem != ItemStack.EMPTY) {
      boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
      this.markDirty();
      if (!didFill) {
        return super.activate(world, pos, state, player, hand, side, hitX, hitY, hitZ);
      }
      return didFill;
    }
    return super.activate(world, pos, state, player, hand, side, hitX, hitY, hitZ);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    tag.setInteger("progress", progress);
    tag.setInteger("lifetime", lifetime);
    return tag;
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    progress = tag.getInteger("progress");
    lifetime = tag.getInteger("lifetime");
  }
}
