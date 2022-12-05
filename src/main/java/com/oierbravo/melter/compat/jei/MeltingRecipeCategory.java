package com.oierbravo.melter.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.HeatSources;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import com.oierbravo.melter.registrate.ModBlocks;
import com.oierbravo.melter.registrate.ModGUITextures;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class MeltingRecipeCategory implements IRecipeCategory<MeltingRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(Melter.MODID, "melting");
    public final static ResourceLocation ARROW_TEXTURE =
            new ResourceLocation(Melter.MODID, "textures/gui/arrow.png");

    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawable heatLevel;
    //protected final IDrawableAnimated arrow;
    //private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public MeltingRecipeCategory(IGuiHelper helper) {
        //this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 59);
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
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.MELTER.get()));

        this.heatLevel = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.MELTER.get()));

    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Class<? extends MeltingRecipe> getRecipeClass() {
        return MeltingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("melting.recipe");
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

        builder.addSlot(RecipeIngredientRole.INPUT, 51, 11).addIngredients(recipe.getIngredient());


            NonNullList<FluidStack> fluidList = NonNullList.create();
            fluidList.add(recipe.getOutput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 11)
                .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(1, new TextComponent(recipe.getOutput().getAmount() + "mB")) )
                .addIngredients(ForgeTypes.FLUID_STACK, fluidList);
        ItemStack minimumHeatItemStack = HeatSources.getItemStackFromMultiplier(recipe.getMinimumHeat());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY,80,28)
                .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(1, new TextComponent("Minimum heat: "+ recipe.getMinimumHeat() )) )
                .addItemStack(minimumHeatItemStack);

    }

    @Override
    public void draw(MeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        ModGUITextures.JEI_SHORT_ARROW.render(stack, 75, 12);
        //IDrawableAnimated arrow = getArrow(recipe);
        //arrow.draw(stack, 75, 12);

        drawProcessingTime(recipe, stack, 81,4);
        //drawRequiredHeat(recipe, stack, 30,35);


    }

    private void drawRequiredHeat(MeltingRecipe recipe, PoseStack stack, int x, int y) {

        int minimumHeat = recipe.getMinimumHeat();
        ItemStack minimumHeatItemStack = HeatSources.getItemStackFromMultiplier(minimumHeat);
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        if(!minimumHeatItemStack.isEmpty()) {
            fontRenderer.draw(stack, "Minimum heat: ", x, y, 0xFF808080);
            fontRenderer.draw(stack,"(" + minimumHeat + ")", x + 79, y, 0xFF808080);
        }
    }

    protected void drawProcessingTime(MeltingRecipe recipe, PoseStack poseStack, int x, int y) {
        int processingTime = recipe.getProcessingTime();
        if (processingTime > 0) {
            int cookTimeSeconds = processingTime / 20;
            TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            fontRenderer.draw(poseStack, timeString, x, y, 0xFF808080);
        }
    }
}
