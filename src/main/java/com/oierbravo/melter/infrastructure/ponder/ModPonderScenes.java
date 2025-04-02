package com.oierbravo.melter.infrastructure.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(com.oierbravo.melter.registrate.ModBlocks.MELTER)
                .addStoryBoard("melter", MelterScenes::melter);
       /* HELPER.forComponents(ModBlocks.SIFTER)
                .addStoryBoard("sifter", MelterScenes::sifter);*/

    }
}
