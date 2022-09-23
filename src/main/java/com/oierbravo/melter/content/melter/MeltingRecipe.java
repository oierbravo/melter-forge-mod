package com.oierbravo.melter.content.melter;

import com.google.gson.JsonObject;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.foundation.fluid.FluidHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


public class MeltingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final NonNullList<FluidStack> output;
    private final NonNullList<Ingredient> input;

    private final int processingTime;
    public MeltingRecipe(ResourceLocation id, NonNullList<FluidStack> output,
                         NonNullList<Ingredient> input, int processingTime) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.processingTime = processingTime;
        validate(id);
    }
    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return input.get(0).test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }


    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return input;
    }
    public int  getProcessingTime() {
        return processingTime;
    }

    public FluidStack getOutputFluidStack() {
        return output.get(0);
    }
    public NonNullList<FluidStack> getOutput() {
        return output;
    }
    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom " + recipeTypeId + " recipe (" + id.toString() + ")";
        Logger logger = Melter.LOGGER;
        int ingredientCount = input.size();
        int outputCount = output.size();

        if (ingredientCount > getMaxInputCount())
            logger.warn(messageHeader + " has more item inputs (" + ingredientCount + ") than supported ("
                    + getMaxInputCount() + ").");

        /*if (outputCount > getMaxOutputCount())
            logger.warn(messageHeader + " has more item outputs (" + outputCount + ") than supported ("
                    + getMaxOutputCount() + ").");*/

        if (processingTime > 0 && !canSpecifyDuration())
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");







        if (outputCount > getMaxFluidOutputCount())
            logger.warn(messageHeader + " has more fluid outputs (" + outputCount + ") than supported ("
                    + getMaxFluidOutputCount() + ").");
    }

    private int getMaxFluidOutputCount() {
        return 1000;
    }

    private boolean canSpecifyDuration() {
        return true;
    }

    private int getMaxInputCount() {
        return 64;
    }

    public int getOutputFluidAmount() {
        return getOutputFluidStack().getAmount();
    }

    public static class Type implements RecipeType<MeltingRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "melting";
    }
    public static class Serializer implements RecipeSerializer<MeltingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Melter.MODID,"melting");

        @Override
        public MeltingRecipe fromJson(ResourceLocation id, JsonObject json) {
            //ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            NonNullList<FluidStack> output = NonNullList.create();

            output.add(FluidHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json,"output")));

            JsonObject ingredients = GsonHelper.getAsJsonObject(json, "input");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            //for (int i = 0; i < inputs.size(); i++) {
                inputs.set(0, Ingredient.fromJson(ingredients));
            //}
            int processingTime = 200;
            if (GsonHelper.isValidNode(json, "processingTime")) {
                processingTime = GsonHelper.getAsInt(json, "processingTime");
            }
            return new MeltingRecipe(id, output, inputs, processingTime);
        }

        @Override
        public MeltingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            NonNullList<FluidStack> output = NonNullList.create();

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            int size = buf.readVarInt();
            for (int i = 0; i < size; i++)
                output.add(FluidStack.readFromPacket(buf));


            int processingTime = buf.readInt();
            return new MeltingRecipe(id, output, inputs,processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MeltingRecipe recipe) {
            NonNullList<FluidStack> output = recipe.output;

            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeVarInt(output.size());
            output.forEach(o -> o.writeToPacket(buf));

            //buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeInt(recipe.getProcessingTime());
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }

    }
}
