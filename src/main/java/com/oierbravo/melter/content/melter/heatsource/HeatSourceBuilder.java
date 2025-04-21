package com.oierbravo.melter.content.melter.heatsource;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HeatSourceBuilder {
    protected ResourceLocation id;
    protected BlockPredicate source;
    protected HeatSource.SourceType sourceType;
    protected int heatLevel;
    protected List<ICondition> conditions;
    protected String description = "";

    public HeatSourceBuilder(ResourceLocation pId) {
        this.id = pId;
        this.source = BlockPredicate.Builder.block().build();
        this.sourceType = HeatSource.SourceType.BLOCK;
        this.heatLevel = 0;
        this.conditions = new ArrayList<>();
    }

    public HeatSourceBuilder source(BlockPredicate pSource){
        this.source = pSource;
        return this;
    }
    public HeatSourceBuilder source(Block pSource){
        return source(BlockPredicate.Builder.block().of(pSource).build());
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
        return new HeatSource(source, heatLevel, sourceType, Optional.of(conditions), Component.literal(description));
    }

    public HeatSource register(ResourceKey<HeatSource> pKey, BootstrapContext<HeatSource> ctx) {
        HeatSource type = build();
        ctx.register(pKey, type);
        return type;
    }

    public HeatSourceBuilder description(String description) {
        this.description = description;
        return this;
    }
}
