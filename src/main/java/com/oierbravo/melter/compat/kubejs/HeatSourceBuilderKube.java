package com.oierbravo.melter.compat.kubejs;

import com.oierbravo.melter.content.melter.heatsource.HeatSource;
import com.oierbravo.melter.content.melter.heatsource.HeatSourceBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class HeatSourceBuilderKube extends BuilderBase<HeatSource> {
    private final HeatSourceBuilder BUILDER;
    public HeatSourceBuilderKube(ResourceLocation id) {
        super(id);
        this.BUILDER = new HeatSourceBuilder(id);
    }
    public HeatSourceBuilderKube source(Block pSource){
        BUILDER.source(pSource);
        return this;
    }
    public HeatSourceBuilderKube heatLevel(int pHeatLevel){
        BUILDER.heatLevel(pHeatLevel);
        return this;
    }
    public HeatSourceBuilderKube sourceType(HeatSource.SourceType pSourceType){
        BUILDER.sourceType(pSourceType);
        return this;
    }
    @Override
    public HeatSource createObject() {
        return BUILDER.build();
    }
}
