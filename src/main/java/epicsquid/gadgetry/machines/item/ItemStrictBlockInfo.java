package epicsquid.gadgetry.machines.item;

import java.util.List;

import epicsquid.gadgetry.core.lib.item.ItemBase;
import epicsquid.gadgetry.core.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStrictBlockInfo extends ItemBase implements IBlockFilter {

  public ItemStrictBlockInfo(String name) {
    super(name);
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    stack.getTagCompound().setString("blockName", IBlockFilter.getBlockName(world, pos, state));
    stack.getTagCompound().setInteger("blockMeta", block.getMetaFromState(state));
    if (world.isRemote) {
      for (int i = 0; i < 10; i++) {
        spawnParticle(world, pos, face);
      }
    }
    world.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.6F,
        1.0F, false);
    return EnumActionResult.SUCCESS;
  }

  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
    if (stack.hasTagCompound()) {
      Block b = Block.getBlockFromName(stack.getTagCompound().getString("blockName"));
      tooltip.add(I18n.format("gadgetrymachines.tooltip.bound_to_block", I18n.format(stack.getTagCompound().getString("blockName") + ".name")));
      tooltip.add(I18n.format("gadgetrymachines.tooltip.block_meta", Integer.toString(stack.getTagCompound().getInteger("blockMeta"))));
    }
  }

  public void spawnParticle(World world, BlockPos pos, EnumFacing face) {
    double d0 = (double) pos.getX() + 0.5;
    double d1 = (double) pos.getY() + 0.5;
    double d2 = (double) pos.getZ() + 0.5;
    Vec3i vec = face.getDirectionVec();
    double x = d0 + vec.getX() * 0.52 + (Util.rand.nextFloat() - 0.5f) * (1.0f - Math.abs(vec.getX()));
    double y = d1 + vec.getY() * 0.52 + (Util.rand.nextFloat() - 0.5f) * (1.0f - Math.abs(vec.getY()));
    double z = d2 + vec.getZ() * 0.52 + (Util.rand.nextFloat() - 0.5f) * (1.0f - Math.abs(vec.getZ()));
    world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, 0, 0, 0, 0);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
    ModelBakery.registerItemVariants(this, getRegistryName(), new ResourceLocation(getRegistryName().toString() + "_empty"));
    ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {

      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        if (stack.hasTagCompound()) {
          return new ModelResourceLocation(getRegistryName(), "inventory");
        } else {
          return new ModelResourceLocation(getRegistryName() + "_empty", "inventory");
        }
      }

    });
  }

  @Override
  public boolean matchesBlock(World world, BlockPos pos, ItemStack filter, IBlockState state) {
    Block block = state.getBlock();
    return IBlockFilter.getBlockName(world, pos, state).equalsIgnoreCase(filter.getTagCompound().getString("blockName"))
        && block.getMetaFromState(state) == filter.getTagCompound().getInteger("blockMeta");
  }
}
