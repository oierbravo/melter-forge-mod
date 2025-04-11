package com.oierbravo.melter.content.melter.heatsource;

import com.oierbravo.melter.foundation.utility.BlockPredicateUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class HeatSourcesConfig {
    public static ModConfigSpec.BooleanValue HEAT_SOURCES_FROM_CONFIG;

    public static ModConfigSpec.ConfigValue<List<? extends List<? extends String>>> HEAT_SOURCES;

    public static void registerServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Settings for heat sources").push("heat_sources");

        HEAT_SOURCES_FROM_CONFIG = builder
                .comment("Read heat sources from config")
                .define("fromConfig", true);

        HEAT_SOURCES = builder
                .comment("List of heat source blocks or fluids. Each element in a list must follow the order: type (block, fluid), name, heat level (1-10), additional information shown in JEI")
                .defineList("heatSources", Arrays.asList(
                    Arrays.asList("block", "minecraft:torch", "1", ""),
                    Arrays.asList("block", "minecraft:soul_torch", "1", ""),
                    Arrays.asList("block", "minecraft:wall_torch", "1", ""),
                    Arrays.asList("block", "minecraft:soul_wall_torch", "1", ""),
                    Arrays.asList("block", "minecraft:fire", "2", ""),
                    Arrays.asList("block", "minecraft:soul_fire", "2", ""),
                    Arrays.asList("block", "minecraft:campfire", "2", ""),
                    Arrays.asList("block", "minecraft:soul_campfire", "2", ""),
                    Arrays.asList("block", "minecraft:magma_block", "3", ""),
                    Arrays.asList("fluid", "minecraft:lava", "5", ""),
                    Arrays.asList("block_state", "create:blaze_burner/fading", "6", "Heated"),
                    Arrays.asList("block_state", "create:blaze_burner/kindled", "6", "Heated"),
                    Arrays.asList("block_state", "create:blaze_burner/smouldering", "8", "Heated"),
                    Arrays.asList("block_state", "create:blaze_burner/seething", "10", "Super-Heated")
                ), entry -> true);
        builder.pop();
    }
    public record ConfigHeatSource(HeatSource.SourceType type, String name, Integer level, String description) {
        public ResourceLocation cleanResourceLocation() {
            // we split by '/' and get the first part, because we don't want the state
            return ResourceLocation.parse(name.split("/")[0]);
        }

        public String getStateString() {
            return name.split("/")[1];
        }
        public boolean hasState(){
            return name.split("/").length == 2;
        }
        public HeatSource toHeatSource(){
            return new HeatSourceBuilder(ResourceLocation.parse(name))
                    .source( BlockPredicateUtils.Builder.build(cleanResourceLocation()))
                    .sourceType(type)
                    .heatLevel(level)
                    .description(description)
                    .build();

        }
    }
}
