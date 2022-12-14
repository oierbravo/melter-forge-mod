package com.oierbravo.melter.compat.kubejs;

import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import net.minecraft.resources.ResourceLocation;

public class KubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {
    /*@Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register("melter:melting", MeltingRecipeProcessing::new);
    }*/
    public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
        event.register(new ResourceLocation("melter:melting"),  MeltingRecipeProcessing::new);
    }

}