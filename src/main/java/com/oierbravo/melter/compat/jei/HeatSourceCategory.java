package com.oierbravo.melter.compat.jei;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.heatsource.CreateHeatSourceUtils;
import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSources;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesConfig;
import com.oierbravo.melter.foundation.utility.BlockPredicateUtils;
import com.oierbravo.melter.registrate.ModBlocks;
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
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

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
        BlockPredicateUtils.Matcher predicateMatcher = BlockPredicateUtils.Matcher.of(recipe.source);
        if (recipe.sourceType == HeatSource.SourceType.FLUID) {
            input.addIngredients(NeoForgeTypes.FLUID_STACK,predicateMatcher.fluidStacks());
        } else {
            List<ItemStack> matchedItemStacks = predicateMatcher.itemStacks();
            if(matchedItemStacks.isEmpty()){
                matchedItemStacks = List.of(getCustomItemStack(recipe.source));
            }

            input.addItemStacks(matchedItemStacks);
        }
        Component customDescription = Component.empty();
        for(Block block : BlockPredicateUtils.Matcher.of(recipe.source).blocks()){
            customDescription = getDespcription(block, recipe.source);//.withStyle(ChatFormatting.ITALIC);
        }
        Component finalCustomDescription = customDescription;
        input.addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(finalCustomDescription));
    }
    private Map<String, ItemStack> getCustomItemStackMap(){
        Map<String,ItemStack> customItemStackMap = new HashMap<>(Map.of());
        customItemStackMap.put("minecraft:fire", new ItemStack(Items.FLINT_AND_STEEL));
        customItemStackMap.put("minecraft:soul_fire", new ItemStack(Items.FLINT_AND_STEEL));

        return customItemStackMap;
    }
    private ItemStack getCustomItemStack(BlockPredicate predicate) {
        Map<String, ItemStack> customMap = getCustomItemStackMap();
        for(Block block:  BlockPredicateUtils.Matcher.of(predicate).blocks()){
            ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(block);

            if(customMap.containsKey(blockResourceLocation.toString()))
                return customMap.get(blockResourceLocation.toString());


        }
        return ItemStack.EMPTY;
    }

    @Override
    public void draw(HeatRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        if(recipe.sourceType == HeatSource.SourceType.CREATIVE) {
            guiGraphics.drawString(minecraft.font, Component.translatable("melter.tooltip.heat_level").append(" ").append(Component.translatable("melter.tooltip.heat_level.creative")), 30, 9, 0xFF808080, false);
            return;
        }
        guiGraphics.drawString(minecraft.font, Component.translatable("melter.tooltip.heat_level").append(Component.literal(" " + recipe.heatLevel)), 30, 9, 0xFF808080, false);
    }
    public static List<HeatRecipe> getRecipes() {
        if(HeatSourcesConfig.HEAT_SOURCES_FROM_CONFIG.get())
            return getRecipesFromConfig();
        return getRecipesFromDatapacks();
    }
    private static List<HeatRecipe> getRecipesFromDatapacks() {
        return List.of();
    }
    private static List<HeatRecipe> getRecipesFromConfig() {
        return HeatSources.getHeatSourcesConfig().stream().map(HeatRecipe::new).toList();
    }

    private Map<String,Component> getDescriptionsMap(){
        Map<String,Component> descriptionMap = new HashMap<>(Map.of());
        descriptionMap.put("melter:creative_heat_source", Component.translatable("melter.tooltip.creative").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze_burner/error", Component.literal("ERROR").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze_burner/none", Component.translatable("melter.tooltip.create.blaze_burner." + "none").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze_burner/fading", Component.translatable("melter.tooltip.create.blaze_burner." + "fading").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze burner/smouldering", Component.translatable("melter.tooltip.create.blaze_burner." + "smouldering").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze_burner/kindled", Component.translatable("melter.tooltip.create.blaze_burner." + "kindled").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        descriptionMap.put("create:blaze_burner/seething", Component.translatable("melter.tooltip.create.blaze_burner." + "seething").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
        return descriptionMap;
    }
    private Component getDespcription(Block block, BlockPredicate predicate){
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        if(Melter.withCreate){
            if(blockId.equals("create:blaze_burner"))
                blockId = CreateHeatSourceUtils.generateCreateBlazeBurnerId(predicate);
        }
        if(getDescriptionsMap().containsKey(blockId)){
            return getDescriptionsMap().get(blockId);
        }
        return Component.empty();

    }
    public static class HeatRecipe{
        public BlockPredicate source;
        public int heatLevel;
        public HeatSource.SourceType sourceType;
        public HeatRecipe(BlockPredicate source, int heatLevel, HeatSource.SourceType sourceType) {
            this.source = source;
            this.heatLevel = heatLevel;
            this.sourceType = sourceType;
        }

        public HeatRecipe(HeatSourcesConfig.ConfigHeatSource configHeatSource){
            this(configHeatSource.toHeatSource());
        }
        public HeatRecipe(HeatSource heatSource){
            this(heatSource.getSource(), heatSource.getHeatLevel(), heatSource.getSourceType());
        }
    }

}
