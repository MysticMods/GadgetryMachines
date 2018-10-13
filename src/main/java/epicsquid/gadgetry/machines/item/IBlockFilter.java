package epicsquid.gadgetry.machines.item;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockFilter {
  public boolean matchesBlock(World world, BlockPos pos, ItemStack filter, IBlockState block);

  public static String getBlockName(World world, BlockPos pos, IBlockState state) {
    String name = Blocks.AIR.getUnlocalizedName();
    Item i = Item.getItemFromBlock(state.getBlock());
    if (i == Items.AIR) {
      if (state.getBlock() instanceof BlockCrops) {
        i = ((BlockCrops) state.getBlock()).getItem(world, pos, state).getItem();
      }
    }
    if (i == Items.AIR) {
      return state.getBlock().getUnlocalizedName();
    }
    return new ItemStack(i, 1, state.getBlock().getMetaFromState(state)).getUnlocalizedName();
  }
}
