package epicsquid.gadgetry.machines.tile;

import epicsquid.gadgetry.core.lib.fluid.ExtendedFluidTank;
import epicsquid.gadgetry.core.lib.tile.TileModular;
import epicsquid.gadgetry.core.lib.tile.module.FaceConfig.FaceIO;
import epicsquid.gadgetry.core.lib.tile.module.Module;
import epicsquid.gadgetry.core.lib.tile.module.ModuleFluid;
import epicsquid.gadgetry.core.predicates.PredicateTrue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class TileFluidTank extends TileModular implements ITickable {

  public static final String TANK = "tank";

  public TileFluidTank() {
    addModule(new ModuleFluid(TANK, this, 1000).addTank(new ExtendedFluidTank(16000, new PredicateTrue(), true)));
    config.setAllIO(FaceIO.IN);
    config.setAllModules(TANK);
  }

  @Override
  public void update() {
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
}
