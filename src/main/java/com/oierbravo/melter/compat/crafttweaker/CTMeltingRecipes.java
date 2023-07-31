package com.oierbravo.melter.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Optional;

@IRecipeHandler.For(MeltingRecipe.class)
@ZenRegister
@ZenCodeType.Name("mods.melter.melting")
public class CTMeltingRecipes implements IRecipeHandler<MeltingRecipe>, IRecipeManager<MeltingRecipe>{

    @ZenCodeGlobals.Global("melter")

    @Override
    public String dumpToCommandString(IRecipeManager<? super MeltingRecipe> manager, MeltingRecipe recipe) {
        return manager.getCommandString() + recipe.toString() + recipe.getOutput() + "[" + recipe.getIngredient() + "]";
    }
    @Override
    public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super MeltingRecipe> manager, MeltingRecipe firstRecipe, U secondRecipe) {
        return false;
    }

    @Override
    public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super MeltingRecipe> manager, MeltingRecipe recipe) {
        return Optional.empty();
    }

    @Override
    public Optional<MeltingRecipe> recompose(IRecipeManager<? super MeltingRecipe> manager, ResourceLocation name, IDecomposedRecipe recipe) {
        return Optional.empty();
    }
    public void addRecipe(String name, IIngredient input   ){
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation(Melter.MODID, name);
        CraftTweakerAPI.apply(new ActionAddRecipe<>( this, new MeltingRecipe(resourceLocation, new FluidStack(Water), input.asVanillaIngredient(), 100, 1)));

    }
    @ZenCodeType.Method
    public void addRecipe(String name, IFluidStack output, IIngredient input, int processingTime, int heatLevel   ){
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation(Melter.MODID, name);
        CraftTweakerAPI.apply(new ActionAddRecipe<>( this, new MeltingRecipe(resourceLocation, output.getInternal(), input.asVanillaIngredient(), processingTime, heatLevel)));
    }

    @Override
    public RecipeType<MeltingRecipe> getRecipeType() {
        return MeltingRecipe.Type.INSTANCE;
    }

    public String fixRecipeName(String name) {
        return name;
    }
}
