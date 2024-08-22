package com.oierbravo.melter.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

public class HeatSourceEvent implements KubeEvent {
    //public static HeatSourceEvent INSTANCE = new HeatSourceEvent();
    public void log(){
        int a = 0;
    }
    public void add(ResourceLocation res, int pHeatLevel, String pSourceType ) {
        /*Block block = BuiltInRegistries.BLOCK.get(res);
        UniqueIdBuilder uniqueBuilder = new UniqueIdBuilder(new StringBuilder());
        uniqueBuilder.append(res);
        String uniqueId = uniqueBuilder.build();
        ResourceKey<HeatSource> resourceKey = ResourceKey.create(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY, ResourceLocation.parse(uniqueId));
        new RegistrySetBuilder()
                .add(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY, bootstrap -> {
                    // Register configured features through the bootstrap context (see below)
                    bootstrap.register(resourceKey, new HeatSource(block, pHeatLevel, HeatSource.SourceType.valueOf(pSourceType)));
                });*/
    }
}