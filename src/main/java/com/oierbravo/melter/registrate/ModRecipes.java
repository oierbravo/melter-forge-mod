package com.oierbravo.melter.registrate;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MeltingRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Melter.MODID);



    public static final RegistryObject<RecipeSerializer<MeltingRecipe>> MELTING_SERIALIZER =
            SERIALIZERS.register("melting", () -> MeltingRecipe.Serializer.INSTANCE);

    public static Optional<MeltingRecipe> find(SimpleContainer pInv, Level pLevel) {
        if(pLevel.isClientSide())
            return Optional.empty();
        return pLevel.getRecipeManager().getRecipeFor(MeltingRecipe.Type.INSTANCE,pInv,pLevel);
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
