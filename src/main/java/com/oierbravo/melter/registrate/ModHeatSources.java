package com.oierbravo.melter.registrate;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSourceBuilder;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesRegistry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

public record ModHeatSources() {
    protected static final ResourceKey<HeatSource>
            TORCH = key("torch_heat_source"),
            FIRE = key("fire_heat_source"),
            CAMPFIRE = key("campfire_heat_source"),
            MAGMA = key("magma_heat_source"),
            LAVA = key("lava_heat_source"),
            CREATIVE = key("creative_heat_source");


    private static ResourceKey<HeatSource> key(String name) {
        return ResourceKey.create(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY, Melter.asResource(name));
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(
                                        HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY,
                                        ModHeatSources::bootstrap
                                ),
                        Set.of(Melter.MODID)
                )
        );
    }

    public static void bootstrap(BootstrapContext<HeatSource> heatSourceBootstrapContext) {
        //Melter
        new HeatSourceBuilder(TORCH.location()).source(Blocks.TORCH).heatLevel(1).register(TORCH, heatSourceBootstrapContext);
        new HeatSourceBuilder(FIRE.location()).source(Blocks.FIRE).heatLevel(2).register(FIRE, heatSourceBootstrapContext);
        new HeatSourceBuilder(CAMPFIRE.location()).source(Blocks.CAMPFIRE).heatLevel(3).register(CAMPFIRE, heatSourceBootstrapContext);
        new HeatSourceBuilder(MAGMA.location()).source(Blocks.MAGMA_BLOCK).heatLevel(4).register(MAGMA, heatSourceBootstrapContext);
        new HeatSourceBuilder(LAVA.location()).source(Blocks.LAVA).heatLevel(5).sourceType(HeatSource.SourceType.FLUID).register(LAVA, heatSourceBootstrapContext);
        new HeatSourceBuilder(CREATIVE.location()).source(ModBlocks.CREATIVE_HEAT_SOURCE_BLOCK.get()).heatLevel(10).register(CREATIVE, heatSourceBootstrapContext);

    }

}
