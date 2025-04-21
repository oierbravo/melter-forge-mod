package com.oierbravo.melter.compat.jei;

import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSources;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesConfig;
import com.oierbravo.melter.foundation.utility.BlockPredicateUtils;
import com.oierbravo.melter.registrate.ModBlocks;
import com.oierbravo.melter.registrate.ModHeatSources;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeatSourceCategory implements IRecipeCategory<HeatSourceCategory.HeatRecipe> {

    public final static RecipeType<HeatRecipe> TYPE = RecipeType.create("melter", "heatsource", HeatRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;

    public HeatSourceCategory(IGuiHelper guiHelper) {
        this.background = new IDrawable() {
            @Override
            public int getWidth() {
                return 176;
            }

            @Override
            public int getHeight() {
                return 25;
            }

            @Override
            public void draw( GuiGraphics graphics, int xOffset, int yOffset) {
            }
        };
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MELTER.get()));
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<HeatRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("heatsource.recipe");
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
    public void setRecipe(IRecipeLayoutBuilder builder, HeatRecipe recipe, IFocusGroup iFocusGroup) {
        var input = builder.addSlot(RecipeIngredientRole.INPUT, 4, 5)
            .setBackground(slotDrawable, -1, -1);
        if (recipe.heatSource.getSourceType() == HeatSource.SourceType.FLUID) {
            input.addIngredients(
                    NeoForgeTypes.FLUID_STACK,
                    BlockPredicateUtils.Matcher.of(recipe.heatSource.getSource())
                            .fluidStacks());
        } else {
            List<ItemStack> itemStacks = recipe.heatSource.asItemStacks();
            if(itemStacks.isEmpty())
                input.addItemStack(getCustomItemStack(recipe.heatSource.getSource()));
            else
                input.addItemStacks(itemStacks);
        }
        input.addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(recipe.heatSource.getDescription()));
    }

    private ItemStack getCustomItemStack(BlockPredicate predicate) {
        Map<String,ItemStack> customItemStackMap = new HashMap<>(Map.of());
        customItemStackMap.put("minecraft:fire", new ItemStack(Items.FLINT_AND_STEEL));
        customItemStackMap.put("minecraft:soul_fire", new ItemStack(Items.FLINT_AND_STEEL));


        for(Block block:  BlockPredicateUtils.Matcher.of(predicate).blocks()){
            ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(block);

            if(customItemStackMap.containsKey(blockResourceLocation.toString()))
                return customItemStackMap.get(blockResourceLocation.toString());

        }
        return ItemStack.EMPTY;
    }

    @Override
    public void draw(HeatRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        if(recipe.heatSource.getSourceType() == HeatSource.SourceType.CREATIVE) {
            guiGraphics.drawString(minecraft.font, Component.translatable("melter.tooltip.heat_level").append(" ").append(Component.translatable("melter.tooltip.heat_level.creative")), 30, 9, 0xFF808080, false);
            return;
        }
        guiGraphics.drawString(minecraft.font, Component.translatable("melter.tooltip.heat_level").append(Component.literal(" " + recipe.heatSource.getHeatLevel())), 30, 9, 0xFF808080, false);
    }
    public static List<HeatRecipe> getRecipes() {
        if(HeatSourcesConfig.HEAT_SOURCES_FROM_CONFIG.get())
            return getRecipesFromConfig();
        return getRecipesFromDatapacks();
    }
    private static List<HeatRecipe> getRecipesFromDatapacks() {
        return ModHeatSources.getAllFromDatapacks().stream().sorted(Comparator.comparingInt(HeatSource::getHeatLevel)).map(HeatRecipe::new).toList();
    }
    private static List<HeatRecipe> getRecipesFromConfig() {
        return HeatSources.getHeatSourcesConfig().stream().map(HeatRecipe::new).toList();
    }

    public static class HeatRecipe{
        public HeatSource heatSource;

        public HeatRecipe(HeatSourcesConfig.ConfigHeatSource configHeatSource){
            this(configHeatSource.toHeatSource());
        }
        public HeatRecipe(HeatSource heatSource){
            this.heatSource = heatSource;
        }
    }

}
