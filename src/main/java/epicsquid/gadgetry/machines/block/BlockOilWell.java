package epicsquid.gadgetry.machines.block;

import java.util.HashMap;
import java.util.Map;

import epicsquid.gadgetry.core.block.BlockTEMulti;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.block.multiblock.BlockMultiblockSlave;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockOilWell extends BlockTEMulti {

  public BlockOilWell(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public Map<BlockPos, IBlockState> getSlavePositions(BlockPos pos, EnumFacing face) {
    Map<BlockPos, IBlockState> map = new HashMap<>();
    IBlockState ioState = ELRegistry.multiblock_slave_modular.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, true);
    IBlockState animState = ELRegistry.multiblock_slave_empty.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, false);

    map.put(pos.up(), ioState);
    map.put(pos.up(2), animState);
    map.put(pos.up(3), animState);
    return map;
  }
}
