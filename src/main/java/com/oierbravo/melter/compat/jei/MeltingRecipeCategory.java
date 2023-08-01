package com.oierbravo.melter.compat.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.HeatSources;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import com.oierbravo.melter.registrate.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class MeltingRecipeCategory implements IRecipeCategory<MeltingRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Melter.MODID, "melting");
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    private final IDrawable background;
    private final IDrawable icon;

    public MeltingRecipeCategory(IGuiHelper helper) {
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
        this.background = new IDrawable() {
            @Override
            public int getWidth() {
                return 176;
            }

            @Override
            public int getHeight() {
                return 45;
            }

            @Override
            public void draw(PoseStack poseStack, int xOffset, int yOffset) {

            }
        };
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MELTER.get()));

    }

    protected IDrawableAnimated getArrow() {
        return this.cachedArrows.getUnchecked(50);
    }
    @Override
    public RecipeType<MeltingRecipe> getRecipeType() {
        return RecipeType.create("melter","melting", MeltingRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("melting.recipe");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull MeltingRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        Ingredient input = recipe.getIngredient();
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 11).addIngredients(recipe.getIngredient());


            NonNullList<FluidStack> fluidList = NonNullList.create();
            fluidList.add(recipe.getOutput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 11)
                .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(1, Component.literal(recipe.getOutput().getAmount() + "mB")) )
                .addIngredients(ForgeTypes.FLUID_STACK, fluidList);
        ItemStack minimumHeatItemStack = HeatSources.getItemStackFromMultiplier(recipe.getHeatLevel());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY,80,28)
                .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(1, Component.literal("Minimum heat: "+ recipe.getHeatLevel() )) )
                .addItemStack(minimumHeatItemStack);

    }

    @Override
    public void draw(MeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        IDrawableAnimated arrow = getArrow();
        arrow.draw(stack, 75, 14);
        drawProcessingTime(recipe, stack, 81,4);


    }
    protected void drawProcessingTime(MeltingRecipe recipe, PoseStack poseStack, int x, int y) {
        int processingTime = recipe.getProcessingTime();
        if (processingTime > 0) {
            int cookTimeSeconds = processingTime / 20;
            MutableComponent timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            fontRenderer.draw(poseStack, timeString, x, y, 0xFF808080);
        }
    }
}
