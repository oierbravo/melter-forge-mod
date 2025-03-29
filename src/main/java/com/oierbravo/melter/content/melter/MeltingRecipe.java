package com.oierbravo.melter.content.melter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oierbravo.melter.registrate.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;


public class MeltingRecipe implements Recipe<RecipeInput> {
    private final FluidStack output;
    private final Ingredient input;

    private final int processingTime;
    private final int heatLevel;

    public static final String ID = "melting";

    protected List<ICondition> conditions = List.of();


    public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            FluidStack.CODEC.fieldOf("output").forGetter(MeltingRecipe::getOutput),
                            Ingredient.CODEC.fieldOf("input").forGetter(MeltingRecipe::getIngredient),
                            Codec.INT.fieldOf("processingTime").forGetter(MeltingRecipe::getProcessingTime),
                            Codec.INT.optionalFieldOf("minimumHeat",1).forGetter(MeltingRecipe::getHeatLevel),
                            ICondition.LIST_CODEC.optionalFieldOf(ConditionalOps.DEFAULT_CONDITIONS_KEY).forGetter(MeltingRecipe::getConditions)
                    ).apply(builder, MeltingRecipe::new)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            FluidStack.STREAM_CODEC,
            MeltingRecipe::getOutput,
            Ingredient.CONTENTS_STREAM_CODEC,
            MeltingRecipe::getIngredient,
            ByteBufCodecs.INT,
            MeltingRecipe::getProcessingTime,
            ByteBufCodecs.INT,
            MeltingRecipe::getHeatLevel,
            MeltingRecipe::new
    );

    public MeltingRecipe(FluidStack pOutput, Ingredient pInput, int pProcessingTime, int pMinimumHeat){

        //validate();
        this(pOutput, pInput, pProcessingTime, pMinimumHeat, Optional.empty());

    }

    public MeltingRecipe(FluidStack pOutput, Ingredient pInput, int pProcessingTime, int pMinimumHeat, Optional<List<ICondition>>  pRecipeConditions) {
        this.output = pOutput;
        this.input = pInput;
        this.processingTime = pProcessingTime;
        this.heatLevel = pMinimumHeat;
        pRecipeConditions.ifPresent(iConditions -> this.conditions = iConditions);
    }


    private Optional<List<ICondition>> getConditions() {
        if(conditions.isEmpty())
            return Optional.empty();
        return Optional.of(conditions);
    }
    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MELTING_TYPE.get();
    }

    public Ingredient getIngredient() {
        return input;
    }
    public int  getProcessingTime() {
        return processingTime;
    }
    public int getHeatLevel() {return heatLevel;}

    public FluidStack getOutput() {
        return output;
    }

    /*private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom " + recipeTypeId + " recipe (" + id.toString() + ")";
        Logger logger = Melter.LOGGER;
        if (processingTime > 0 && !canSpecifyDuration())
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");
    }*/

    private boolean canSpecifyDuration() {
        return true;
    }

    public int getOutputFluidAmount() {
        return getOutput().getAmount();
    }



    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

    @Override
    public MapCodec<MeltingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }
}
