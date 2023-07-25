package com.oierbravo.melter.compat.kubejs;

import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface MeltingRecipeSchema {
    RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key("input");
    RecipeKey<OutputFluid> RESULT = FluidComponents.OUTPUT.key("output");
    RecipeKey<Integer> PROCESSING_TIME = NumberComponent.INT.key("processingTime").optional(100);
    RecipeKey<Integer> MINIMUM_HEAT = NumberComponent.INT.key("minimumHeat").optional(0);


    public class MeltingRecipe extends RecipeJS{

    }
    RecipeSchema SCHEMA = new RecipeSchema(MeltingRecipe.class, MeltingRecipe::new, RESULT, INGREDIENT, PROCESSING_TIME, MINIMUM_HEAT);

}
