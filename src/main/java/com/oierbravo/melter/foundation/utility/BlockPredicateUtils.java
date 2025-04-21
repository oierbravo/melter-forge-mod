package com.oierbravo.melter.foundation.utility;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BlockPredicateUtils {
    public static class Builder {
        public static BlockPredicate.Builder from(Block block){
            return BlockPredicate.Builder.block().of(block);
        }
        public static BlockPredicate.Builder from(ResourceLocation resourceLocation){
            return from(BuiltInRegistries.BLOCK.get(resourceLocation));
        }
        public static BlockPredicate build(Block block){
            return from(block).build();
        }
        public static BlockPredicate build(ResourceLocation resourceLocation){
            return build(BuiltInRegistries.BLOCK.get(resourceLocation));
        }
        public static BlockPredicate build(String id){
            return build(ResourceLocation.parse(id));
        }
    }
    public static class Matcher {
        public static final BlockPredicate EMPTY = new BlockPredicate(Optional.empty(), Optional.empty(), Optional.empty());
        final BlockPredicate blockPredicate;
        public Matcher(BlockPredicate blockPredicate){
            this.blockPredicate = blockPredicate;
        }
        public static Matcher of(BlockPredicate blockPredicate) {
            return new Matcher(blockPredicate);
        }

        public boolean isEmpty() {
            if (blockPredicate == EMPTY) return true;
            return hasEmptyBlocks() && hasEmptyProperties() && hasEmptyNbt();
        }

        public boolean hasEmptyBlocks() {
            return blockPredicate.blocks().isEmpty();
        }

        public boolean hasEmptyProperties() {
            return blockPredicate.properties().isEmpty();
        }

        public boolean hasEmptyNbt() {
            return blockPredicate.nbt().isEmpty();
        }

        public List<Block> blocks() {
            if (isEmpty())
                return List.of();

            final List<Block> blocks = new java.util.ArrayList<>();
            if (blockPredicate.blocks().isPresent()) {
                for (Holder<Block> block : blockPredicate.blocks().get().unwrap().map(BuiltInRegistries.BLOCK::getOrCreateTag, Function.identity())) {
                    blocks.add(block.value());
                }
            }
            return blocks;
        }


        public List<Fluid> fluids() {
            return blocks().stream()
                    .filter(block -> block instanceof LiquidBlock)
                    .map(block -> ((LiquidBlock) block).fluid.getSource())
                    .toList();
        }

        public List<FluidStack> fluidStacks() {
            return fluids().stream()
                    .map(fluid -> new FluidStack(fluid, 1000))
                    .toList();
        }

        public List<Item> items() {
            if (isEmpty()) {
                return List.of();
            }
            return blocks().stream()
                    .map(Block::asItem)
                    .filter(item ->
                            !item.equals(Items.AIR))
                    .toList();
        }

        public List<ItemStack> itemStacks() {
            if (isEmpty()) {
                return List.of();
            }
            List<Item> items = items();
            return items().stream()
                    .map(Item::getDefaultInstance)
                    .toList();
        }

    }
}