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

public class BlockDistiller extends BlockTEMultiHoriz implements IMultiblock {

  public BlockDistiller(Material mat, SoundType type, float hardness, String name, Class<? extends TileEntity> teClass) {
    super(mat, type, hardness, name, teClass);
  }

  @Override
  public Map<BlockPos, IBlockState> getSlavePositions(BlockPos pos, EnumFacing face) {
    Map<BlockPos, IBlockState> map = new HashMap<>();
    IBlockState ioState = ELRegistry.multiblock_slave_modular.getDefaultState().withProperty(BlockMultiblockSlave.SHADOW, true);
    if (face == EnumFacing.UP) {
      for (EnumFacing e : EnumFacing.VALUES) {
        if (e.getIndex() > 1) {
          Map<BlockPos, IBlockState> m = getSlavePositions(pos, e);
          for (Entry<BlockPos, IBlockState> en : m.entrySet()) {
            map.put(en.getKey(), en.getValue());
          }
        }
      }
      return map;
    }
    for (int i = 0; i < 2; i++) {
      map.put(pos.up(i).offset(face, -1), ioState);
      map.put(pos.up(i).offset(face.rotateYCCW(), -1), ioState);
      map.put(pos.up(i).offset(face, -1).offset(face.rotateYCCW(), -1), ioState);
    }
    map.put(pos.up(), ioState);
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

  public EnumFacing getFace(float hitX, float hitZ) {
    if (hitX < 0.5 && hitZ < 0.5)
      return EnumFacing.NORTH;
    else if (hitX >= 0.5 && hitZ < 0.5)
      return EnumFacing.EAST;
    else if (hitX >= 0.5 && hitZ >= 0.5)
      return EnumFacing.SOUTH;
    else
      return EnumFacing.WEST;
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getDefaultState().withProperty(facing, getFace(hitX, hitZ).getOpposite());
  }
}
