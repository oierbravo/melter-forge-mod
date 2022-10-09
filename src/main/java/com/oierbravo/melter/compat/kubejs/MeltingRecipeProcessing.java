package com.oierbravo.melter.compat.kubejs;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.recipe.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class MeltingRecipeProcessing extends RecipeJS {
    public FluidStackJS outputFluid;
    public Ingredient inputIngredient;
    public MeltingRecipeProcessing(){

    }
    @Override
    public void create(RecipeArguments recipeArguments) {
        //for (var result : ListJS.orSelf(recipeArguments.get(0))) {
        //outputItems.add(parse)

        outputFluid = parseOutputFluid(recipeArguments.get(0));

//        }
        inputIngredient = parseItemInput(recipeArguments.get(1));

        //for (var input : ListJS.orSelf(args.get(1))) {
        //    inputItems.add(parseIngredientItem(input));
        //}

        json.addProperty("processingTime", 100);
    }
    public FluidStackJS parseOutputFluid(@Nullable Object o) {
        FluidStackJS result = FluidStackJS.of(o);
        if (result != null && !result.isEmpty()) {
            return result;
        } else {
            throw new RecipeExceptionJS("" + o + " is not a valid result!");
        }
    }

    @Override
    public void deserialize() {

        inputIngredient = parseItemInput(json.get("input"));
        outputFluid = parseOutputFluid(json.get("output"));
    }

    @Override
    public void serialize() {
        if (serializeOutputs) {
            json.add("output",outputFluid.toJson());
        }
        if (serializeInputs) {
            json.add("input", inputIngredient.toJson());
        }



    }

    @Override
    public boolean hasInput(IngredientMatch match) {
        return match.contains(this.inputIngredient);
    }


    public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
        if (match.contains(this.inputIngredient)) {
            this.inputIngredient = transformer.transform(this, match, this.inputIngredient, with);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasOutput(IngredientMatch ingredientMatch) {
        return false;
    }

    @Override
    public boolean replaceOutput(IngredientMatch ingredientMatch, ItemStack itemStack, ItemOutputTransformer itemOutputTransformer) {
        return false;
    }

    public MeltingRecipeProcessing processingTime(int time) {
        json.addProperty("processingTime", time);
        save();
        return this;
    }
}