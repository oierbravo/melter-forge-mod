package com.oierbravo.melter.compat.kubejs;

import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;

public class KubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {
    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register("melter:melting", MeltingRecipeProcessing::new);
    }

}