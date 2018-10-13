package epicsquid.gadgetry.machines.recipe;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import epicsquid.gadgetry.core.lib.event.RegisterJEICategoriesEvent;
import epicsquid.gadgetry.core.lib.event.RegisterJEIHandlingEvent;
import epicsquid.gadgetry.core.lib.gui.GuiModular;
import epicsquid.gadgetry.core.lib.util.Util;
import epicsquid.gadgetry.machines.GadgetryMachines;
import epicsquid.gadgetry.machines.GadgetryMachinesContent;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CombustionValuesJEI {

  public static String RECIPE_UID = GadgetryMachines.MODID + ":" + Util.getLowercaseClassName(CombustionValues.class);

  protected static class RecipeWrapper implements IRecipeWrapper {
    Fluid fluid = null;
    int value = 0;

    public RecipeWrapper(Fluid f, int val) {
      this.fluid = f;
      this.value = val;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
      ingredients.setInput(FluidStack.class, new FluidStack(fluid, 1000));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int width, int height, int mx, int my) {
      Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("" + value * 1000 + " FE", 100, 12, 0xFFFFFF);
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
      return I18n.format("gadgetrymachines.jei.combustion.title");
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
      if (offY == -1) {
        offY = 0;
      }

      IGuiFluidStackGroup fluids = layout.getFluidStacks();
      fluids.init(0, true, 26, 1, 16, 32, 1000, true, null);
      fluids.set(0, ingredients.getInputs(FluidStack.class).get(0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Minecraft minecraft) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(GadgetryMachines.MODID + ":textures/gui/jei.png"));
      Gui.drawModalRectWithCustomSizedTexture(25, 0, 0, 0, 18, 34, 256, 256);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiModular.baseTexture);
      Gui.drawModalRectWithCustomSizedTexture(60, 8, 176, 0, 24, 16, 256, 256);
    }
  }

  @SubscribeEvent
  public void registerJEIHandler(RegisterJEIHandlingEvent event) {
    event.getRegistry().addRecipes(CombustionValues.values.entrySet(), RECIPE_UID);
    event.getRegistry().handleRecipes(Entry.class, recipe -> new RecipeWrapper((Fluid) recipe.getKey(), (Integer) recipe.getValue()), RECIPE_UID);
    event.getRegistry().addRecipeCatalyst(new ItemStack(GadgetryMachinesContent.combustion_gen, 1), RECIPE_UID);
  }

  @SubscribeEvent
  public void registerJEICategory(RegisterJEICategoriesEvent event) {
    event.getRegistry().addRecipeCategories(new RecipeCategory(event.getRegistry().getJeiHelpers().getGuiHelper()));
  }
}
