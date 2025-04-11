package com.oierbravo.melter.content.melter;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MeltingRecipeBuilder implements RecipeBuilder {
    protected MeltingRecipeParams params;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected List<ICondition> recipeConditions;

    @Nullable
    protected String group = "melting";

    public MeltingRecipeBuilder(ResourceLocation id){
        params = new MeltingRecipeParams(id);
        recipeConditions = new ArrayList<>();
    }

    public MeltingRecipeBuilder input(ItemLike itemLike){
        params.input = Ingredient.of(itemLike);
        return this;
    }

    public MeltingRecipeBuilder input(ResourceLocation rl){
        return input(BuiltInRegistries.BLOCK.get(rl));
    }

    public MeltingRecipeBuilder input(String id){
        return input(ResourceLocation.parse(id));
    }
    public MeltingRecipeBuilder input(TagKey<Item> tag) {
        params.input = Ingredient.of(tag);
        return this;
    }
    public MeltingRecipeBuilder output(Fluid pFluid, int pAmount) {
        params.output = new FluidStack(pFluid, pAmount);
        return this;
    }

    public MeltingRecipeBuilder withProcessingTime(int processingTime) {
        params.processingTime = processingTime;
        return this;
    }

    public MeltingRecipeBuilder requiredHeat(int pHeatLevel) {
        params.minimumHeat = pHeatLevel;
        return this;
    }


    @Override
    public @NotNull MeltingRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull MeltingRecipeBuilder group(@Nullable String pGroup) {
        this.group = pGroup;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return Items.AIR;
    }



    public MeltingRecipeBuilder whenModLoaded(String modid) {
        return withCondition(new ModLoadedCondition(modid));
    }
    public MeltingRecipeBuilder withCondition(ICondition condition) {
        recipeConditions.add(condition);
        return this;
    }

    public MeltingRecipe build(){
        return new MeltingRecipe(params.output,params.input,params.processingTime,params.minimumHeat, Optional.of(recipeConditions));
    }


    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        recipeOutput.accept(resourceLocation, build(), advancement.build(params.id.withPrefix("recipes/")));
    }
    @Override
    public void save(RecipeOutput recipeOutput) {
        save(recipeOutput, params.id);
    }



    public static class MeltingRecipeParams {
        protected ResourceLocation id;
        protected FluidStack output;
        protected Ingredient input;
        protected int processingTime;
        protected int minimumHeat;

        protected MeltingRecipeParams(ResourceLocation id) {

            this.id = id;
            output = FluidStack.EMPTY;
            input = Ingredient.EMPTY;
            processingTime = 200;
            minimumHeat = 1;
        }

    }
}
