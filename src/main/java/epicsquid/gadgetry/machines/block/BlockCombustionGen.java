package epicsquid.gadgetry.machines.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import epicsquid.gadgetry.core.block.BlockTEMultiOnOffHoriz;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.block.multiblock.BlockMultiblockSlave;
import epicsquid.gadgetry.core.lib.block.multiblock.IMultiblock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCombustionGen extends BlockTEMultiOnOffHoriz implements IMultiblock {

  public BlockCombustionGen(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public Map<BlockPos, IBlockState> getSlavePositions(BlockPos pos, EnumFacing face) {
    Map<BlockPos, IBlockState> map = new HashMap<>();
    IBlockState ioState = ELRegistry.multiblock_slave_modular.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, false);
    map.put(pos.up(), ioState);
    return map;
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state.getValue(active)) {
      return 15;
    } else {
      Set<BlockPos> positions = getSlavePositions(pos, EnumFacing.UP).keySet();
      int[] lmap = new int[positions.size()];
      int ct = 0;
      for (BlockPos p : positions) {
        lmap[ct] = world.getCombinedLight(pos, 1);
        ct++;
      }
      int max = lmap[0];
      for (int i = 1; i < lmap.length; i++) {
        if (lmap[i] > max) {
          max = lmap[i];
        }
      }
      if (world.getCombinedLight(pos, 1) >= 14) {
        return 0;
      }
      return max;
    }
  }
}
