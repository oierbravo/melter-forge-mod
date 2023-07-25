package com.oierbravo.melter.compat.kubejs;

import com.oierbravo.melter.content.melter.MeltingRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class KubeJSPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {
    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(MeltingRecipe.Serializer.ID, MeltingRecipeSchema.SCHEMA);
    }

}