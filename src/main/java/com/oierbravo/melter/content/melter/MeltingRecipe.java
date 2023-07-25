package com.oierbravo.melter.content.melter;

import com.google.gson.JsonObject;
import com.oierbravo.melter.Melter;
import com.oierbravo.melter.foundation.fluid.FluidHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;


public class MeltingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    //private final NonNullList<FluidStack> output;
    //private final NonNullList<Ingredient> input;

    private final FluidStack output;
    private final Ingredient input;

    private final int processingTime;
    private final int heatLevel;
    public MeltingRecipe(ResourceLocation id, FluidStack output,
                         Ingredient input, int processingTime, int heatLevel) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.processingTime = processingTime;
        this.heatLevel = heatLevel;
        validate(id);
    }
    @Override
    public boolean matches(SimpleContainer pContainer, @NotNull Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return null;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input);
        return nonnulllist;
    }

    public Ingredient getIngredient() {
        return input;
    }
    public int  getProcessingTime() {
        return processingTime;
    }
    public int getHeatLevel() {return heatLevel;}

    public FluidStack getOutputFluidStack() {
        return output;
    }
    public FluidStack getOutput() {
        return output;
    }
    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom " + recipeTypeId + " recipe (" + id.toString() + ")";
        Logger logger = Melter.LOGGER;
        if (processingTime > 0 && !canSpecifyDuration())
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");
    }

    private boolean canSpecifyDuration() {
        return true;
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

            FluidStack output = FluidHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json,"output"));

            Ingredient input =  Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            int processingTime = 200;
            if (GsonHelper.isValidNode(json, "processingTime")) {
                processingTime = GsonHelper.getAsInt(json, "processingTime");
            }
            int minimumHeat = 0;
            if (GsonHelper.isValidNode(json, "minimumHeat")) {
                minimumHeat = GsonHelper.getAsInt(json, "minimumHeat");
            }
            return new MeltingRecipe(id, output, input, processingTime, minimumHeat);
        }

        @Override
        public MeltingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            FluidStack output = FluidStack.readFromPacket(buf);


            int processingTime = buf.readInt();
            int minimumHeat = buf.readInt();
            return new MeltingRecipe(id, output, input,processingTime, minimumHeat);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MeltingRecipe recipe) {
            recipe.input.toNetwork(buf);
            recipe.output.writeToPacket(buf);
            buf.writeInt(recipe.getProcessingTime());
            buf.writeInt(recipe.getHeatLevel());
        }
    }
}
