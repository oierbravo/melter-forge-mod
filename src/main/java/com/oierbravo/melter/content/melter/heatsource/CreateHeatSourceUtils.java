package com.oierbravo.melter.content.melter.heatsource;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CreateHeatSourceUtils {
    public static String appendheatLevel(BlockState state) {
        String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            BlazeBurnerBlock.HeatLevel heatLevel = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            // can't have a second colon here, see ResourceLocation#assertValidNamespace
            blockName += "/" + heatLevel.getSerializedName();
        }
        return blockName;
    }

    public static String generateCreateBlazeBurnerId(BlockPredicate predicate) {
        String baseId = "create:blaze_burner/";
        for(BlazeBurnerBlock.HeatLevel heatLevel : BlazeBurnerBlock.HeatLevel.values()) {
            if (predicate.properties().get().matches(createBlazeBurnerBlockState(heatLevel)))
                return baseId + heatLevel.getSerializedName();
        }
        return baseId + "error";
    }
    public static BlockState createBlazeBurnerBlockState(BlazeBurnerBlock.HeatLevel heatLevel){
        return AllBlocks.BLAZE_BURNER.getDefaultState().setValue(BlazeBurnerBlock.HEAT_LEVEL, heatLevel);
    }
    public static Component createDescription(BlockPredicate predicate){
        return Component.translatable("block.create.blaze_burner");
    }
}
