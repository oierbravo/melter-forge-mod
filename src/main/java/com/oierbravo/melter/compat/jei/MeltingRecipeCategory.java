package com.oierbravo.melter.compat.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSources;
import com.oierbravo.melter.registrate.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class MeltingRecipeCategory implements IRecipeCategory<MeltingRecipe> {

    public final static RecipeType<MeltingRecipe> TYPE = RecipeType.create("melter", "melting", MeltingRecipe.class );


    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Melter.MODID, "melting");
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    private final IDrawable background;
    private final IDrawable icon;
    //protected final IDrawableAnimated arrow;
    //private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    private final IDrawable slotDrawable;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public MeltingRecipeCategory(IGuiHelper helper) {
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return helper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(Melter.MODID,"textures/gui/arrow.png"), 22, 0, 22, 16)
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
                return 55;
            }

            @Override
            public void draw( GuiGraphics graphics, int xOffset, int yOffset) {

            }
        };
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MELTER.get()));
        this.slotDrawable = helper.getSlotDrawable();
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

    @SuppressWarnings("removal")
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
        int inputSlotOffsetX = 51;

        Ingredient input = recipe.getIngredient();
        builder.addSlot(RecipeIngredientRole.INPUT, inputSlotOffsetX, 14)
                .addIngredients(input)
                .setBackground(slotDrawable, -1, -1);

        NonNullList<FluidStack> fluidList = NonNullList.create();
        fluidList.add(recipe.getOutput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, getWidth()-inputSlotOffsetX - slotDrawable.getWidth(), 14)
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal(recipe.getOutput().getAmount() + "mB").withStyle(ChatFormatting.GOLD)) )
                .addIngredients(NeoForgeTypes.FLUID_STACK, fluidList)
                .setBackground(slotDrawable, -1, -1);
        
        Map<HeatSource.SourceType, List> heatSourceStacks = HeatSources.getHeatSourcesForHeatLevel(recipe.getHeatLevel());;
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY,getWidth()/2 - slotDrawable.getWidth()/2,38)
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.melting.recipe.minimum_heat", recipe.getHeatLevel()).withStyle(ChatFormatting.GOLD)))
                .addIngredients(NeoForgeTypes.FLUID_STACK, (List<FluidStack>) heatSourceStacks.get(HeatSource.SourceType.FLUID))
                .addItemStacks((List<ItemStack>) heatSourceStacks.get(HeatSource.SourceType.BLOCK))
                .setBackground(slotDrawable, -1, -1);
    }

    @Override
    public void draw(MeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        IDrawableAnimated arrow = getArrow();
        arrow.draw(graphics, getWidth()/2 - arrow.getWidth()/2, 15);
        drawProcessingTime(recipe, graphics);
    }

    protected void drawProcessingTime(MeltingRecipe recipe, GuiGraphics graphics) {
        int processingTime = recipe.getProcessingTime();
        if (processingTime > 0) {
            int cookTimeSeconds = processingTime / 20;
            MutableComponent timeString = Component.literal(decimalFormat.format(cookTimeSeconds) + "s");
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            graphics.drawString(fontRenderer, timeString, getWidth()/2 - stringWidth/2, 2, 0xFF808080, false);
        }
    }
}
