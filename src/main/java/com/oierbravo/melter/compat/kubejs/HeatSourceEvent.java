package com.oierbravo.melter.compat.kubejs;

import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesRegistry;
import com.oierbravo.melter.foundation.utility.BlockPredicateUtils;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class HeatSourceEvent implements KubeEvent {
    public void add(ResourceLocation res, int pHeatLevel, String pSourceType ) {
        Block block = BuiltInRegistries.BLOCK.get(res);
        UniqueIdBuilder uniqueBuilder = new UniqueIdBuilder(new StringBuilder());
        uniqueBuilder.append(res);
        String uniqueId = uniqueBuilder.build();
        ResourceKey<HeatSource> resourceKey = ResourceKey.create(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY, ResourceLocation.parse(uniqueId));
        new RegistrySetBuilder()
                .add(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY, bootstrap -> {
                    // Register configured features through the bootstrap context (see below)
                    bootstrap.register(resourceKey, new HeatSource(BlockPredicateUtils.Builder.build(block), pHeatLevel, HeatSource.SourceType.valueOf(pSourceType)));
                });
    }
}