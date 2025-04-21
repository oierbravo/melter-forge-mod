package com.oierbravo.melter.foundation.data;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MeltingRecipeBuilder;
import com.simibubi.create.AllFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class MeltingRecipeGen extends RecipeProvider {

    public MeltingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        create("cobblestones")
                .input(Tags.Items.COBBLESTONES)
                .output(Fluids.LAVA.getSource(),250)
                .withProcessingTime(200)
                .requiredHeat(1)
                .save(recipeOutput);

        create("stones")
                .input(Tags.Items.STONES)
                .output(Fluids.LAVA.getSource(),250)
                .withProcessingTime(200)
                .requiredHeat(1)
                .save(recipeOutput);

        create("obsidians")
                .input(Tags.Items.OBSIDIANS)
                .output(Fluids.LAVA.getSource(),1000)
                .withProcessingTime(400)
                .requiredHeat(5)
                .save(recipeOutput);

        create("honey")
                .input(Blocks.HONEY_BLOCK)
                .output(AllFluids.HONEY.get(), 1000)
                .withProcessingTime(400)
                .requiredHeat(1)
                .whenModLoaded("create")
                .save(recipeOutput);

        create("snow_ball")
                .input(Items.SNOWBALL)
                .output(Fluids.WATER.getSource(), 250)
                .withProcessingTime(400)
                .requiredHeat(1)
                .save(recipeOutput);

        create("snow")
                .input(Blocks.SNOW_BLOCK)
                .output(Fluids.WATER.getSource(), 500)
                .withProcessingTime(400)
                .requiredHeat(1)
                .save(recipeOutput);

        create("ice")
                .input(Blocks.ICE)
                .output(Fluids.WATER.getSource(), 1000)
                .withProcessingTime(400)
                .requiredHeat(1)
                .save(recipeOutput);

        create("blue_ice")
                .input(Blocks.BLUE_ICE)
                .output(Fluids.WATER.getSource(), 1000)
                .withProcessingTime(400)
                .requiredHeat(1)
                .save(recipeOutput);

        create("leaves")
                .input(ItemTags.LEAVES)
                .output(Fluids.WATER.getSource(), 100)
                .withProcessingTime(400)
                .requiredHeat(1)
                .save(recipeOutput);


    }

    private MeltingRecipeBuilder create(String id){
        return new MeltingRecipeBuilder(Melter.asResource("melting/" + id));
    }

    @Override
    public final String getName() {
        return "Melter's melting recipes.";
    }

}
