package com.oierbravo.melter.content.melter.heatsource;

import com.oierbravo.melter.Melter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.Map;
import java.util.Optional;

public class HeatSourcesRegistry {

    public static final ResourceKey<Registry<HeatSource>> HEAT_SOURCE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Melter.MODID, "heat_sources"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                HEAT_SOURCE_REGISTRY_KEY,
                HeatSource.CODEC,
                HeatSource.CODEC
        );
    }

    public static Optional<HeatSource> fromBlockState(Level pLevel, BlockInWorld pBlock){
        for (Map.Entry<ResourceKey<HeatSource>, HeatSource> entry : pLevel.registryAccess().registry(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY).get().entrySet()) {
            HeatSource heatSource = entry.getValue();
            if(heatSource.test(pBlock))
                return Optional.of(heatSource);
        }
        return Optional.empty();
    }



}
