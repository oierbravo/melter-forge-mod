package com.oierbravo.melter.content.melter.heatsource;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeatSourceBuilder {
    protected ResourceLocation id;
    protected Block source;
    protected HeatSource.SourceType sourceType;
    protected int heatLevel;
    protected List<ICondition> conditions;

    public HeatSourceBuilder(ResourceLocation pId) {
        this.id = pId;
        this.source = Blocks.AIR;
        this.sourceType = HeatSource.SourceType.BLOCK;
        this.heatLevel = 0;
        this.conditions = new ArrayList<>();
    }

    public HeatSourceBuilder source(Block pSource){
        this.source = pSource;
        return this;
    }
    public HeatSourceBuilder heatLevel(int pHeatLevel){
        this.heatLevel = pHeatLevel;
        return this;
    }
    public HeatSourceBuilder sourceType(HeatSource.SourceType pSourceType){
        this.sourceType = pSourceType;
        return this;
    }
    public HeatSourceBuilder whenModLoaded(String modid) {
        return withCondition(new ModLoadedCondition(modid));
    }
    public HeatSourceBuilder withCondition(ICondition condition) {
        conditions.add(condition);
        return this;
    }
    public HeatSource build() {
        return new HeatSource(source, heatLevel, sourceType, Optional.of(conditions));
    }

    public HeatSource register(ResourceKey<HeatSource> pKey, BootstrapContext<HeatSource> ctx) {
        HeatSource type = build();
        ctx.register(pKey, type);
        return type;
    }
}
