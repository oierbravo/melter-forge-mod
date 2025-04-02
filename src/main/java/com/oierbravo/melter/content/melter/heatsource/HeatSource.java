package com.oierbravo.melter.content.melter.heatsource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oierbravo.melter.Melter;
import com.simibubi.create.AllBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class HeatSource {

    protected final int heatLevel;
    protected final BlockPredicate source;
    protected final SourceType sourceType;
    protected List<ICondition> conditions = List.of();
    protected Component customDescription = Component.empty();

    public static final Codec<HeatSource> CODEC = RecordCodecBuilder.create(instance -> // Given an instance
            instance.group( // Define the fields within the instance
                    BlockPredicate.CODEC.fieldOf("source").forGetter(HeatSource::getSource),
                    Codec.INT.fieldOf("heatLevel").forGetter(HeatSource::getHeatLevel),
                    StringRepresentable.fromEnum(SourceType::values).fieldOf("sourceType").forGetter(HeatSource::getSourceType),
                    ICondition.LIST_CODEC.optionalFieldOf(ConditionalOps.DEFAULT_CONDITIONS_KEY).forGetter(HeatSource::getConditions)
            ).apply(instance, HeatSource::new) // Define how to create the object
    );

    private Optional<List<ICondition>> getConditions() {
        if(conditions.isEmpty())
            return Optional.empty();
        return Optional.of(conditions);
    }


    public static final Codec<Optional<WithConditions<HeatSource>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC);

    public SourceType getSourceType() {
        return this.sourceType;
    }
    public HeatSource(BlockPredicate pSource, int pHeatLevel, SourceType pSourceType, Optional<List<ICondition>> conditions){
        this.source = pSource;
        this.heatLevel = pHeatLevel;
        this.sourceType = pSourceType;
        conditions.ifPresent(iConditions -> this.conditions = iConditions);
    }


    public int getHeatLevel() {
        return this.heatLevel;
    }

    public BlockPredicate getSource() {
        return this.source;
    }
    public boolean test(BlockInWorld pBlock){
        return this.source.matches(pBlock);
    }
    public ItemStack asItemStack(){
        ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(source.blocks().get().get(0).value());
        //"minecraft:soul_fire" -> generateItemStackWithCustomItemName(new ItemStack(Items.FIRE_CHARGE),Component.translatable("block.minecraft.soul_fire").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
        return switch(blockResourceLocation.toString()) {
            case "minecraft:fire" -> HeatSourceUtils.generateItemStackWithCustomItemName(new ItemStack(Items.FLINT_AND_STEEL), Component.translatable("block.minecraft.fire").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            case "minecraft:soul_fire" -> HeatSourceUtils.generateItemStackWithCustomItemName(new ItemStack(Items.FIRE_CHARGE),Component.translatable("block.minecraft.soul_fire").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD));
            case "create:blaze_burner" -> Melter.withCreate ? HeatSourceUtils.generateItemStackWithCustomItemName(new ItemStack(AllBlocks.BLAZE_BURNER), getDescription()): ItemStack.EMPTY;
            default -> BuiltInRegistries.BLOCK.get(blockResourceLocation).asItem().getDefaultInstance();
        };
    }

    public Fluid asFluidSource() {
        ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(source.blocks().get().get(0).value());
        return BuiltInRegistries.FLUID.get(blockResourceLocation);
    }
    public FluidStack asFluidStackSource() {
        ResourceLocation blockResourceLocation = BuiltInRegistries.BLOCK.getKey(source.blocks().get().get(0).value());
        return new FluidStack(BuiltInRegistries.FLUID.get(blockResourceLocation),1000);
    }

    public boolean isCreative() {
        if(sourceType == SourceType.CREATIVE)
            return true;
        return false;
    }
    public void setDescription(Component description){
        this.customDescription = description;
    }
    public MutableComponent getDescription(){
        return (MutableComponent) customDescription;
    }

    public enum SourceType implements StringRepresentable  {
        BLOCK, FLUID, CREATIVE, BLOCK_STATE;


        @Override
        public @NotNull String getSerializedName() {
            return this.name();
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
