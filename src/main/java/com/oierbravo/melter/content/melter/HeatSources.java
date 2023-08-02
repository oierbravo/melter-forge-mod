package com.oierbravo.melter.content.melter;

import com.oierbravo.melter.Melter;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public enum HeatSources implements StringRepresentable {
    NONE(0,"", "None"),
    TORCH(1,"Block{minecraft:torch}", "Torch"),
    WALL_TORCH(1,"Block{minecraft:torch}", "Torch"),
    CAMPFIRE(2,"Block{minecraft:campfire}", "Campfire"),
    LAVA(4,"Block{minecraft:lava}", "Lava"),
    BLAZE_BURNER_INACTIVE(8,"create:blocks/blaze_burner:smouldering", "Blaze burner"),
    BLAZE_BURNER_FADING(9,"create:blocks/blaze_burner:fading","Blaze burner"),
    BLAZE_BURNER_ACTIVE(10,"create:blocks/blaze_burner:kindled","Blaze burner"),
    BLAZE_BURNER_SUPERHEATED(16,"create:blocks/blaze_burner:seething","SUPERHEATED!");
    // CREATE HeatLevel: NONE, SMOULDERING, FADING, KINDLED, SEETHING,;
    private int multiplier;
    private String resourceName;
    private String displayName;
    HeatSources (int multiplier, String resourceName, String displayName){
        this.multiplier = multiplier;
        this.resourceName = resourceName;
        this.displayName = displayName;
    }

    public static HeatSources fromLevel(Level level, BlockPos below) {
        BlockState belowBlockState = level.getBlockState(below);
        return HeatSources.get(belowBlockState);
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getResourceName() {
        return resourceName;
    }

    public static HeatSources get(int minimumHeat){
        return Arrays.stream(HeatSources.values())
                .filter(e -> e.multiplier > minimumHeat)
                .findFirst()
                .orElse(HeatSources.NONE);
    }
    public static HeatSources get(String resourceName){
        return Arrays.stream(HeatSources.values())
                .filter(e -> e.resourceName.equals(resourceName))
                .findFirst()
                .orElse(HeatSources.NONE);
    }
    public static HeatSources get(BlockState blockState){

        String nameString = blockState.getBlock().getLootTable().toString();
        String blockString = blockState.getBlock().toString();
        if(blockString.equals(HeatSources.CAMPFIRE.getResourceName())){
            if(!blockState.getValue(BlockStateProperties.LIT)){
                return HeatSources.NONE;
            }
        }
        if(Melter.withCreate && (blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL))){
            BlazeBurnerBlock.HeatLevel heatLevel = blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            nameString += ":" +heatLevel.getSerializedName();
            return get(nameString);
        }

        return get(blockString);
    }
    public static boolean isHeatSource(BlockState blockState){
        HeatSources source = HeatSources.get(blockState);
        if(source != HeatSources.NONE){
            return true;
        }
        return false;
    }
    public static ItemStack getItemStackFromMultiplier(int multiplier){
        HeatSources heatSource = get(multiplier);
        if(heatSource == HeatSources.NONE)
            return ItemStack.EMPTY;

        String resourceName = heatSource.getResourceName();
        if(!resourceName.matches("create:blocks/blaze_burner(.*)")){
            return new ItemStack(Items.TORCH);
        }
        resourceName = "create:blaze_burner";
        ResourceLocation resourceLocation = ResourceLocation.tryParse(resourceName);
        return new ItemStack(ForgeRegistries.ITEMS.getValue(resourceLocation));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getDisplayName() {
        return displayName;
    }
}
