/*

package com.oierbravo.melter.compat.kubejs;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.kubejs.fluid.UnboundFluidStackJS;
import dev.latvian.mods.kubejs.recipe.*;
import dev.latvian.mods.rhino.NativeJavaObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class MeltingRecipeProcessing extends RecipeJS {
    public FluidIngredient outputFluid;
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
        json.addProperty("minimumHeat", 0);
    }
    public FluidIngredient parseOutputFluid(@Nullable Object o) {

        if(o instanceof NativeJavaObject){
            NativeJavaObject object = (NativeJavaObject) o;
            Object a =  object.unwrap();
            //if(a instanceof )
            UnboundFluidStackJS unboundFluidStackJS = (UnboundFluidStackJS) object.unwrap();
            if (unboundFluidStackJS != null && !unboundFluidStackJS.isEmpty()) {
                return FluidIngredient.fromFluid(unboundFluidStackJS.getFluid(), (int) unboundFluidStackJS.getAmount());
            }
            throw new RecipeExceptionJS("" + o + " is not a valid result!");
        }
        throw new RecipeExceptionJS("" + o + " is not a valid result!");
    }

    @Override
    public void deserialize() {
        inputIngredient = parseItemInput(json.get("input"));
        outputFluid = parseOutputFluid(json.getAsJsonArray("output"));
    }

    @Override
    public void serialize() {
        if (serializeOutputs) {
            json.add("output", outputFluid.serialize());
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
    public MeltingRecipeProcessing minimumHeat(int minimum) {
        json.addProperty("minimumHeat", minimum);
        save();
        return this;
    }

}
*/