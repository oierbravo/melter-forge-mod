package com.oierbravo.melter.content.melter

import com.google.gson.JsonObject
import com.oierbravo.melter.Melter
import com.oierbravo.melter.foundation.fluid.FluidHelper
import net.minecraft.core.NonNullList
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraftforge.fluids.FluidStack

class MeltingRecipe(
    private val id: ResourceLocation, //private final NonNullList<FluidStack> output;
    //private final NonNullList<Ingredient> input;
    val outputFluidStack: FluidStack,
    val ingredient: Ingredient, @JvmField val processingTime: Int, @JvmField val heatLevel: Int
) : Recipe<SimpleContainer> {

    init {
        validate(id)
    }

    override fun matches(pContainer: SimpleContainer, pLevel: Level): Boolean {
        return ingredient.test(pContainer.getItem(0))
    }

    override fun assemble(pContainer: SimpleContainer): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean {
        return true
    }

    override fun getResultItem(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getId(): ResourceLocation {
        return id
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Serializer.INSTANCE
    }

    override fun getType(): RecipeType<*> {
        return Type.INSTANCE
    }

    override fun getIngredients(): NonNullList<Ingredient> {
        val nonnulllist = NonNullList.create<Ingredient>()
        nonnulllist.add(ingredient)
        return nonnulllist
    }

    private fun validate(recipeTypeId: ResourceLocation) {
        val messageHeader = "Your custom $recipeTypeId recipe ($id)"
        val logger = Melter.LOGGER
        if (processingTime > 0 && !canSpecifyDuration()) logger.warn("$messageHeader specified a duration. Durations have no impact on this type of recipe.")
    }

    private fun canSpecifyDuration(): Boolean {
        return true
    }

    val outputFluidAmount: Int
        get() = outputFluidStack.amount

    object Type : RecipeType<MeltingRecipe?> {
        @JvmField
        val INSTANCE: Type = Type
        const val ID = "melting"
    }

    class Serializer : RecipeSerializer<MeltingRecipe> {
        override fun fromJson(id: ResourceLocation, json: JsonObject): MeltingRecipe {
            val output = FluidHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "output"))
            val input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"))
            var processingTime = 200
            if (GsonHelper.isValidNode(json, "processingTime")) {
                processingTime = GsonHelper.getAsInt(json, "processingTime")
            }
            var minimumHeat = 0
            if (GsonHelper.isValidNode(json, "minimumHeat")) {
                minimumHeat = GsonHelper.getAsInt(json, "minimumHeat")
            }
            return MeltingRecipe(id, output, input, processingTime, minimumHeat)
        }

        override fun fromNetwork(id: ResourceLocation, buf: FriendlyByteBuf): MeltingRecipe {
            val input = Ingredient.fromNetwork(buf)
            val output = FluidStack.readFromPacket(buf)
            val processingTime = buf.readInt()
            val minimumHeat = buf.readInt()
            return MeltingRecipe(id, output, input, processingTime, minimumHeat)
        }

        override fun toNetwork(buf: FriendlyByteBuf, recipe: MeltingRecipe) {
            recipe.ingredient.toNetwork(buf)
            recipe.outputFluidStack.writeToPacket(buf)
            buf.writeInt(recipe.processingTime)
            buf.writeInt(recipe.heatLevel)
        }

        companion object {
            @JvmField
            val INSTANCE = Serializer()
            val ID = ResourceLocation(Melter.MODID, "melting")
        }
    }
}