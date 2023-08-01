package com.oierbravo.melter.compat.crafttweaker;


import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.melter.MeltingManager")
//@Document("mods/melter/melting")
public class MeltingRecipeManager implements IRecipeManager<MeltingRecipe> {
    /**
     * Adds a Melting recipe.
     *
     * @param name     The name of the recipe
     * @param output  The output fluidStack of the recipe.
     * @param input    The input of the recipe.
     * @param processingTime The duration of the recipe (default 100 ticks)
     * @param heatLevel Minimum heat level
     *
     * @docParam name "meltDown"
     * @docParam output <fluid:minecraft:lava> * 100
     * @docParam input <item:minecraft:dirt>
     * @docParam duration 200
     * @docParam heatLevel 2
     */
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
}
