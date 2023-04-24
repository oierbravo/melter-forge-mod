package com.oierbravo.melter.content.melter

import com.oierbravo.melter.Melter
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.ForgeRegistries
import java.util.*

enum class HeatSources(// CREATE HeatLevel: NONE, SMOULDERING, FADING, KINDLED, SEETHING,;
    @JvmField val multiplier: Int, val resourceName: String, @JvmField val displayName: String
) : StringRepresentable {
    NONE(0, "", "None"), TORCH(1, "Block{minecraft:torch}", "Torch"), WALL_TORCH(
        1,
        "Block{minecraft:torch}",
        "Torch"
    ),
    CAMPFIRE(2, "Block{minecraft:campfire}", "Campfire"), LAVA(
        4,
        "Block{minecraft:lava}",
        "Lava"
    ),
    BLAZE_BURNER_INACTIVE(8, "create:blocks/blaze_burner:smouldering", "BB.Inactive"), BLAZE_BURNER_FADING(
        9,
        "create:blocks/blaze_burner:fading",
        "BB.Active"
    ),
    BLAZE_BURNER_ACTIVE(10, "create:blocks/blaze_burner:kindled", "BB.Active"), BLAZE_BURNER_SUPERHEATED(
        16,
        "create:blocks/blaze_burner:seething",
        "SUPERHEATED!"
    );

    override fun getSerializedName(): String {
        return name.lowercase(Locale.getDefault())
    }

    override fun toString(): String {
        return super.toString()
    }

    companion object {
        @JvmStatic
        fun fromLevel(level: Level, below: BlockPos?): HeatSources {
            val belowBlockState = level.getBlockState(below)
            return Companion[belowBlockState]
        }

        operator fun get(minimumHeat: Int): HeatSources {
            return Arrays.stream(values())
                .filter { e: HeatSources -> e.multiplier > minimumHeat }
                .findFirst()
                .orElse(NONE)
        }

        operator fun get(resourceName: String): HeatSources {
            return Arrays.stream(values())
                .filter { e: HeatSources -> e.resourceName == resourceName }
                .findFirst()
                .orElse(NONE)
        }

        @JvmStatic
        operator fun get(blockState: BlockState): HeatSources {
            var nameString = blockState.block.lootTable.toString()
            val blockString = blockState.block.toString()
            if (Melter.withCreate && blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                val heatLevel = blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL)
                nameString += ":" + heatLevel.serializedName
                return Companion[nameString]
            }
            return Companion[blockString]
        }

        @JvmStatic
        fun isHeatSource(blockState: BlockState): Boolean {
            val source = Companion[blockState]
            return if (source != NONE) {
                true
            } else false
        }

        @JvmStatic
        fun getItemStackFromMultiplier(multiplier: Int): ItemStack {
            val heatSource = Companion[multiplier]
            if (heatSource == NONE) return ItemStack.EMPTY
            var resourceName = heatSource.resourceName
            if (!resourceName.matches("create:blocks/blaze_burner(.*)".toRegex())) {
                return ItemStack(Items.TORCH)
            }
            resourceName = "create:blaze_burner"
            val resourceLocation = ResourceLocation.tryParse(resourceName)
            return ItemStack(ForgeRegistries.ITEMS.getValue(resourceLocation))
        }
    }
}