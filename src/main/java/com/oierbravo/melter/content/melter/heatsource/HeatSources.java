package com.oierbravo.melter.content.melter.heatsource;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.registrate.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

import static com.oierbravo.melter.content.melter.heatsource.HeatSourceUtils.generateItemStackWithCustomItemName;

public class HeatSources {
    public static int MAX_LEVEL = 20;
    public static int fromLevel(Level pLevel, BlockPos below) {
        BlockInWorld belowInWorld = new BlockInWorld(pLevel,below, false);

        return HeatSources.getHeatSource(pLevel, belowInWorld);
    }

    public static boolean isCreative(Level pLevel, BlockPos below) {
        BlockInWorld belowInWorld = new BlockInWorld(pLevel,below, false);

        Optional<HeatSource> heatSource = HeatSourcesRegistry.fromBlockState(pLevel, belowInWorld);
        return heatSource.map(HeatSource::isCreative).orElse(false);
    }

    public static boolean isHeatSource(Level pLevel, BlockInWorld block) {

        Optional<HeatSource> heatSource = HeatSourcesRegistry.fromBlockState(pLevel, block);

        return heatSource.isPresent();
    }

    public static int getHeatSource(Level pLevel, BlockInWorld block){
        if(HeatSourcesConfig.HEAT_SOURCES_FROM_CONFIG.get())
            return getHeatSourceFromConfig(block);
        return getHeatSourceFromDatapack(pLevel, block);
    }
    public static int getHeatSourceFromDatapack(Level pLevel, BlockInWorld block) {
        Optional<HeatSource> heatSource = HeatSourcesRegistry.fromBlockState(pLevel, block);

        if(heatSource.isEmpty())
            return 0;

        if(heatSource.get().getSourceType() == HeatSource.SourceType.BLOCK ||
                heatSource.get().getSourceType() == HeatSource.SourceType.BLOCK_STATE)
            return heatSource.get().getHeatLevel();


        // fluid
        int liquidLevel = 0;
        if (block.getState().getBlock() instanceof LiquidBlock liquidBlock) {
            liquidLevel = block.getState().getValue(LiquidBlock.LEVEL);
        }
        float liquidLevelDecay = liquidLevel <= 0 ? 1 : liquidLevel + .5f; // 1.2 is some small delta for the 'decay'

        int heatLevel = heatSource.get().heatLevel;
        // subtract fluid level decay; if liquidLevel is 0, heatLevel is returned
        heatLevel = (int)((float)heatLevel / liquidLevelDecay);

        return heatLevel;
    }
    public static int getHeatSourceFromConfig(BlockInWorld blockInWorld) {
        // creative
        BlockState state = blockInWorld.getState();
        if (state.getBlock().equals(ModBlocks.CREATIVE_HEAT_SOURCE_BLOCK.get())) {
            return 20;
        }

        Block block = state.getBlock();
        String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        // campfire state
        if (state.hasProperty(CampfireBlock.LIT)) {
            boolean isLit = state.getValue(CampfireBlock.LIT);
            if (!isLit) {
                return 0;
            }
        }

        // create
        if (Melter.withCreate) {
            // blaze burner
            blockName = CreateHeatSourceUtils.appendheatLevel(state);

        }

        // fluid
        int liquidLevel = 0;
        if (block instanceof LiquidBlock liquidBlock) {
            ResourceLocation fluidRL = BuiltInRegistries.FLUID.getKey(liquidBlock.fluid.getSource());
            blockName = fluidRL.toString();

            if (!state.getFluidState().isSource()) {
                liquidLevel = state.getValue(LiquidBlock.LEVEL);
            }
        }
        float liquidLevelDecay = liquidLevel <= 0 ? 1 : liquidLevel + 1.2f; // 1.2 is some small delta for the 'decay'

        int heatLevel = getConfigHeatSourceMap().getOrDefault(blockName, 0);

        // subtract fluid level decay; if liquidLevel is 0, heatLevel is returned
        heatLevel = (int)((float)heatLevel / liquidLevelDecay);

        return heatLevel;
    }
    public static List<HeatSourcesConfig.ConfigHeatSource> getHeatSourcesConfig() {
        return HeatSourcesConfig.HEAT_SOURCES.get()
            .stream()
            .map(s -> {
                try {
                    return new HeatSourcesConfig.ConfigHeatSource(HeatSource.SourceType.valueOf(s.get(0).toUpperCase()), s.get(1), Integer.valueOf(s.get(2)), s.get(3));
                }
                catch (Exception e) {
                    Melter.LOGGER.error("Failed to load heat source: {}", s);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }

    public static Map<String, Integer> getConfigHeatSourceMap() {
        Map<String, Integer> map = new HashMap<>();
        List<? extends List<? extends String>> sources = HeatSourcesConfig.HEAT_SOURCES.get();
        HeatSourcesConfig.HEAT_SOURCES.get()
            .forEach(s -> {
                if (!map.containsKey(s.get(1))) {
                    map.put(s.get(1), Integer.valueOf(s.get(2)));
                }
            });
        return map;
    }

    /**
     * There isn't a good way to display both FluidStacks and ItemStacks in a JEI recipe.
     * Here, we are getting every valid heat source for a specific level.
     * A map is returned with a 'type' as a key and a list of 'anything', but really either a FluidStack or an ItemStack.
     * There are then passed to MeltingRecipeCategory to display the textures.
     *
     * If no valid heat source is present, we are adding a Barrier block with a custom text.
     */
    public static Map<HeatSource.SourceType, List> getHeatSourcesForHeatLevelFromConfig(int heatLevel) {
        Map<HeatSource.SourceType, List> stackMap = new HashMap<>(); // I know...
        Arrays.stream(HeatSource.SourceType.values()).forEach(type -> stackMap.put(type, new ArrayList<>()));

        if (heatLevel > 20) {
            stackMap.put(HeatSource.SourceType.BLOCK, List.of(generateItemStackWithCustomItemName(new ItemStack(Blocks.BARRIER),Component.translatable("melter.tooltip.no_source_found"))));
            return stackMap;
        }

        HeatSourcesConfig.HEAT_SOURCES.get().stream()
            .filter(s -> Integer.valueOf(s.get(2)).equals(heatLevel))
            .map(e -> new HeatSourcesConfig.ConfigHeatSource(HeatSource.SourceType.valueOf(e.get(0).toUpperCase()), e.get(1), Integer.valueOf(e.get(2)), e.get(3)))
            .filter(e -> {
                try {
                    e.cleanResourceLocation();
                    return true;
                }
                catch (Exception ex) {
                    Melter.LOGGER.error("Can't process name '{}'", e.name());
                    return false;
                }
            })
            .forEach(e -> {
                var rl = e.cleanResourceLocation();
                if (e.type().equals(HeatSource.SourceType.BLOCK)) {
                    // Fire and Soul Fire don't really have a "Block" we can use to texture
                    ItemStack is = switch(rl.toString()) {
                        case "minecraft:fire" -> generateItemStackWithCustomItemName(new ItemStack(Items.FLINT_AND_STEEL),Component.translatable("block.minecraft.fire").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                        case "minecraft:soul_fire" -> generateItemStackWithCustomItemName(new ItemStack(Items.FIRE_CHARGE),Component.translatable("block.minecraft.soul_fire").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
                        //case "create:lit_blaze_burner" -> Melter.withCreate ? new ItemStack(AllBlocks.BLAZE_BURNER) : new ItemStack(Blocks.AIR);
                        default -> new ItemStack(BuiltInRegistries.ITEM.get(rl));
                    };

                    boolean isItemStackPresent = stackMap.get(HeatSource.SourceType.BLOCK).stream()
                        .anyMatch(stack -> ((ItemStack) stack).is(is.getItem()));

                    if (!isItemStackPresent && !is.getItem().equals(new ItemStack(Blocks.AIR).getItem())) {
                        if (!e.description().isEmpty()) {
                            stackMap.get(HeatSource.SourceType.BLOCK).add(generateItemStackWithCustomItemName(is,
                                    is.getHoverName().copy().append(Component.literal(" (" + e.description() + ")"))));
                        }
                        else {
                            stackMap.get(HeatSource.SourceType.BLOCK).add(is);
                        }
                    }
                }

                if (e.type().equals(HeatSource.SourceType.FLUID)) {
                    FluidStack fs = new FluidStack(BuiltInRegistries.FLUID.get(rl), 1000);

                    boolean isFluidStackPresent = stackMap.get(HeatSource.SourceType.FLUID).stream()
                        .anyMatch(stack -> ((FluidStack) stack).is(fs.getFluidType()));

                    if (!isFluidStackPresent) {
                        stackMap.get(HeatSource.SourceType.FLUID).add(fs);
                    }
                }
            });

        Boolean isMapEmpty = stackMap.values().stream()
            .map(List::isEmpty)
            .reduce(Boolean::logicalAnd)
            .orElse(true);

        if (isMapEmpty) {
            stackMap.put(HeatSource.SourceType.BLOCK, List.of(generateItemStackWithCustomItemName(new ItemStack(Blocks.BARRIER),Component.translatable("melter.tooltip.no_source_found"))));
            return getHeatSourcesForHeatLevelFromConfig(heatLevel + 1);
        }

        return stackMap;
    }
    public static Map<HeatSource.SourceType, List> getHeatSourcesForHeatLevelFromDatapacks(int heatLevel) {
        Map<HeatSource.SourceType, List> stackMap = new HashMap<>(); // I know...
        Arrays.stream(HeatSource.SourceType.values()).forEach(type -> stackMap.put(type, new ArrayList<>()));

        Minecraft.getInstance().level
                .registryAccess()
                .registry(HeatSourcesRegistry.HEAT_SOURCE_REGISTRY_KEY)
                .get().entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(heatSource -> heatSource.getHeatLevel() == heatLevel)
                .forEach(
                        heatSource -> {
                            if(heatSource.getSourceType() == HeatSource.SourceType.BLOCK
                            || heatSource.getSourceType() == HeatSource.SourceType.BLOCK_STATE)
                                stackMap.get(heatSource.getSourceType())
                                        .add(heatSource.asItemStack());
                            if(heatSource.getSourceType() == HeatSource.SourceType.FLUID)
                                stackMap.get(heatSource.getSourceType())
                                        .add(heatSource.asFluidStackSource());
                        }
                );

        if (heatLevel > 20) {
            stackMap.put(HeatSource.SourceType.BLOCK, List.of(generateItemStackWithCustomItemName(new ItemStack(Blocks.BARRIER),Component.translatable("melter.tooltip.no_source_found"))));
            return stackMap;
        }

        Boolean isMapEmpty = stackMap.values().stream()
                .map(List::isEmpty)
                .reduce(Boolean::logicalAnd)
                .orElse(true);

        if (isMapEmpty) {
            stackMap.put(HeatSource.SourceType.BLOCK, List.of(generateItemStackWithCustomItemName(new ItemStack(Blocks.BARRIER),Component.translatable("melter.tooltip.no_source_found"))));
            return getHeatSourcesForHeatLevelFromDatapacks(heatLevel + 1);
        }

        return stackMap;
    }


    public static Map<HeatSource.SourceType, List> getHeatSourcesForHeatLevel(int heatLevel) {
        if(HeatSourcesConfig.HEAT_SOURCES_FROM_CONFIG.get())
            return getHeatSourcesForHeatLevelFromConfig(heatLevel);
        return  getHeatSourcesForHeatLevelFromDatapacks(heatLevel);

    }

    @Override
    public String toString() {
        return super.toString();
    }


}
