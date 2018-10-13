package epicsquid.gadgetry.machines.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import epicsquid.gadgetry.core.block.BlockTEMultiHoriz;
import epicsquid.gadgetry.core.lib.ELRegistry;
import epicsquid.gadgetry.core.lib.block.multiblock.BlockMultiblockSlave;
import epicsquid.gadgetry.core.lib.block.multiblock.IMultiblock;
import epicsquid.gadgetry.core.lib.tile.multiblock.IMaster;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDrill extends BlockTEMultiHoriz implements IMultiblock {

  public BlockDrill(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public Map<BlockPos, IBlockState> getSlavePositions(BlockPos pos, EnumFacing face) {
    Map<BlockPos, IBlockState> map = new HashMap<>();
    IBlockState ioState = ELRegistry.multiblock_slave_modular.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, true);
    IBlockState empty = ELRegistry.multiblock_slave_empty.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, false);
    for (int j = 0; j < 3; j++) {
      for (int i = -1; i < 2; i++) {
        for (int k = -1; k < 2; k++) {
          if (!(i == 0 && j == 0 && k == 0)) {
            map.put(pos.add(i, j, k), empty);
          }
        }
      }
      if (face.getIndex() > 1) {
        map.put(pos.up(j).offset(face), ioState);
        map.put(pos.up(j).offset(face).offset(face.rotateY()), ioState);
        map.put(pos.up(j).offset(face).offset(face.rotateYCCW()), ioState);
        map.remove(pos.up(j).offset(face, -1));
        map.remove(pos.up(j).offset(face, -1).offset(face.rotateY()));
        map.remove(pos.up(j).offset(face, -1).offset(face.rotateYCCW()));
      }
    }
    return map;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity t = world.getTileEntity(pos);
    if (t instanceof IMaster) {
      Map<BlockPos, IBlockState> m = getSlavePositions(pos, state.getValue(facing));
      for (Entry<BlockPos, IBlockState> p : m.entrySet()) {
        addSlave(world, (IMaster) t, pos, p.getKey(), p.getValue());
      }
    }
    t.markDirty();
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getDefaultState().withProperty(facing, placer.getHorizontalFacing().getOpposite());
  }
}
