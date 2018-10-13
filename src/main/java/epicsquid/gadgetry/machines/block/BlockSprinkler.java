package epicsquid.gadgetry.machines.block;

import epicsquid.gadgetry.core.block.BlockTEHoriz;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockSprinkler extends BlockTEHoriz {
  public static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

  public BlockSprinkler(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return BOUNDS;
  }
}
