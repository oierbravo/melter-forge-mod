package com.oierbravo.melter.registrate;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.heatsource.*;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;

public record ModHeatSources() {
    protected static final ResourceKey<HeatSource>
            TORCH = key("torch_heat_source"),
            FIRE = key("fire_heat_source"),
            CAMPFIRE = key("campfire_heat_source"),
            MAGMA = key("magma_heat_source"),
            LAVA = key("lava_heat_source"),
            CREATIVE = key("creative_heat_source"),
            BLAZE_SMOULDERING = key("create_blaze_burner_smoldering"),
            BLAZE_FADING = key("create_blaze_burner_fading"),
            BLAZE_KINDLED = key("create_blaze_burner_kindled"),
            BLAZE_SEETHING = key("create_blaze_burner_seething");

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
        new HeatSourceBuilder(CAMPFIRE.location()).source(BlockPredicate.Builder.block().of(Blocks.CAMPFIRE).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CampfireBlock.LIT,true)).build()).heatLevel(3).register(CAMPFIRE, heatSourceBootstrapContext);
        new HeatSourceBuilder(MAGMA.location()).source(Blocks.MAGMA_BLOCK).heatLevel(4).register(MAGMA, heatSourceBootstrapContext);
        new HeatSourceBuilder(LAVA.location()).source(Blocks.LAVA).heatLevel(5).sourceType(HeatSource.SourceType.FLUID).register(LAVA, heatSourceBootstrapContext);
        new HeatSourceBuilder(CREATIVE.location()).source(ModBlocks.CREATIVE_HEAT_SOURCE_BLOCK.get()).sourceType(HeatSource.SourceType.CREATIVE).register(CREATIVE, heatSourceBootstrapContext);
        new HeatSourceBuilder(BLAZE_SMOULDERING.location()).source(BlockPredicate.Builder.block().of(AllBlocks.BLAZE_BURNER.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING)).build()).heatLevel(6).sourceType(HeatSource.SourceType.BLOCK_STATE).whenModLoaded("create").register(BLAZE_SMOULDERING, heatSourceBootstrapContext);
        new HeatSourceBuilder(BLAZE_FADING.location()).source(BlockPredicate.Builder.block().of(AllBlocks.BLAZE_BURNER.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.FADING)).build()).heatLevel(6).sourceType(HeatSource.SourceType.BLOCK_STATE).whenModLoaded("create").register(BLAZE_FADING, heatSourceBootstrapContext);
        new HeatSourceBuilder(BLAZE_KINDLED.location()).source(BlockPredicate.Builder.block().of(AllBlocks.BLAZE_BURNER.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED)).build()).heatLevel(8).sourceType(HeatSource.SourceType.BLOCK_STATE).whenModLoaded("create").register(BLAZE_KINDLED, heatSourceBootstrapContext);
        new HeatSourceBuilder(BLAZE_SEETHING.location()).source(BlockPredicate.Builder.block().of(AllBlocks.BLAZE_BURNER.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SEETHING)).build()).heatLevel(10).sourceType(HeatSource.SourceType.BLOCK_STATE).whenModLoaded("create").register(BLAZE_SEETHING, heatSourceBootstrapContext);

    }
    public static List<HeatSource> getAll(){
        if(HeatSourcesConfig.HEAT_SOURCES_FROM_CONFIG.get())
            return getAllFromConfigs();
        return getAllFromDatapacks();
    }
    private static List<HeatSource> getAllFromConfigs() {
        return HeatSources.getHeatSourcesConfig().stream().map(HeatSourcesConfig.ConfigHeatSource::toHeatSource).toList();
    }
    public static List<HeatSource> getAllFromDatapacks() {
        assert Minecraft.getInstance().level != null;
        return Minecraft.getInstance().level.registryAccess().registry(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY).get().entrySet().stream().map(Map.Entry::getValue).toList();
    }

    /*public static List<HeatSourceCategory.HeatRecipe> getRecipesFromConfig() {
        List<HeatSourceCategory.HeatRecipe> recipes = new ArrayList<>();

        for (HeatSources.ConfigHeatSource hs : HeatSources.getHeatSourcesConfig()) {
            Melter.LOGGER.info("processing heat source: " + hs.name());
            var rl = hs.cleanResourceLocation();

            BlockPredicate.Builder blockPredicate = BlockPredicateUtils.Builder.from(hs.cleanResourceLocation());

            if(Melter.withCreate && rl.toString().equals("create:blaze_burner")){
                blockPredicate = blockPredicate.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.valueOf(hs.getStateString())));
            }
            recipes.add(new HeatSourceCategory.HeatRecipe(blockPredicate.build(),hs.level(), hs.type()));

        }

        // creative
        recipes.add(new HeatSourceCategory.HeatRecipe( BlockPredicateUtils.Builder.build(ModBlocks.CREATIVE_HEAT_SOURCE_BLOCK.get()), Integer.MAX_VALUE, HeatSource.SourceType.CREATIVE));

        // Sort by heat ascending
        recipes.sort(Comparator.comparingInt(HeatSourceCategory.HeatRecipe::heat));

        Melter.LOGGER.info("Added '{}' heat sources to JEI", recipes.size());

        return recipes;
    }*/

}
