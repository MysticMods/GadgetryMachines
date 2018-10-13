package epicsquid.gadgetry.machines.recipe;

import javax.annotation.Nullable;

import epicsquid.gadgetry.core.lib.ELEvents;
import epicsquid.gadgetry.core.lib.event.RegisterJEICategoriesEvent;
import epicsquid.gadgetry.core.lib.event.RegisterJEIHandlingEvent;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.util.Util;
import epicsquid.gadgetry.machines.GadgetryMachines;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DistillingRecipeJEI {

  public static String RECIPE_UID = GadgetryMachines.MODID + ":" + Util.getLowercaseClassName(DistillingRecipe.class);

  protected static class RecipeWrapper implements IRecipeWrapper {
    DistillingRecipe recipe;

    public RecipeWrapper(DistillingRecipe r) {
      recipe = r;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
      ingredients.setInput(ItemStack.class, recipe.inputs.get(0));
      ingredients.setInput(FluidStack.class, recipe.fluid_in);
      ingredients.setOutput(ItemStack.class, recipe.getOutput());
      ingredients.setOutput(FluidStack.class, recipe.fluid_out);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int width, int height, int mx, int my) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(GadgetryMachines.MODID + ":textures/gui/jei.png"));
      Gui.drawModalRectWithCustomSizedTexture(17, 0, 0, 0, 18, 34, 256, 256);
      Gui.drawModalRectWithCustomSizedTexture(141, 0, 0, 0, 18, 34, 256, 256);
    }
  }

  protected static class RecipeCategory implements IRecipeCategory {
    protected IDrawable background, icon;
    public static ResourceLocation texture = new ResourceLocation("elulib:textures/gui/container.png");
    public static int offY = -1;

    public RecipeCategory(IGuiHelper helper) {
      background = helper.createDrawable(texture, 0, 0, 180, 34, 0, 0);
    }

    @Override
    public String getUid() {
      return RECIPE_UID;
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    public IDrawable getIcon() {
      return icon;
    }

    @Override
    public String getTitle() {
      return I18n.format("gadgetrymachines.jei.distiller.title");
    }

    @Override
    public String getModName() {
      return GadgetryMachines.MODID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IDrawable getBackground() {
      return background;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
      IGuiFluidStackGroup fluids = layout.getFluidStacks();
      fluids.init(0, true, 18, 1, 16, 32, 1000, true, null);
      fluids.set(0, ingredients.getInputs(FluidStack.class).get(0));
      fluids.init(1, false, 142, 1, 16, 32, 1000, true, null);
      fluids.set(1, ingredients.getOutputs(FluidStack.class).get(0));

      IGuiItemStackGroup stacks = layout.getItemStacks();
      stacks.init(0, true, 38, 9);
      stacks.set(0, ingredients.getInputs(ItemStack.class).get(0));
      stacks.init(1, true, 116, 9);
      stacks.set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Minecraft minecraft) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiModular.baseTexture);
      Gui.drawModalRectWithCustomSizedTexture(38, 9, 208, 32, 18, 18, 256, 256);
      Gui.drawModalRectWithCustomSizedTexture(112, 5, 176, 32, 26, 26, 256, 256);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(GadgetryMachines.MODID + ":textures/gui/progress_bars.png"));
      Gui.drawModalRectWithCustomSizedTexture(62, 10, 48, 48, 44, 16, 256, 256);
      Gui.drawModalRectWithCustomSizedTexture(62, 10, 48, 32, (int) ((44f * (ELEvents.ticks % 100)) / 100f), 16, 256, 256);

    }
  }

  @SubscribeEvent
  public void registerJEIHandler(RegisterJEIHandlingEvent event) {
    event.getRegistry().addRecipes(DistillingRecipe.recipes, RECIPE_UID);
    event.getRegistry().handleRecipes(DistillingRecipe.class, recipe -> new RecipeWrapper(recipe), RECIPE_UID);
    event.getRegistry().addRecipeCatalyst(new ItemStack(GadgetryMachinesContent.distiller, 1), RECIPE_UID);
  }

  @SubscribeEvent
  public void registerJEICategory(RegisterJEICategoriesEvent event) {
    event.getRegistry().addRecipeCategories(new RecipeCategory(event.getRegistry().getJeiHelpers().getGuiHelper()));
  }
}
