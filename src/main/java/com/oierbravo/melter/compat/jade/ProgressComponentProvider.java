package com.oierbravo.melter.compat.jade;

import com.oierbravo.melter.content.melter.MelterBlockEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ProgressComponentProvider  implements IComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        //CompoundTag serverData = accessor.getServerData();
        if (accessor.getServerData().contains("melter.progress")) {
            int progress = accessor.getServerData().getInt("melter.progress");
            if(progress > 0)
                tooltip.add(new TranslatableComponent("melter.tooltip.progress", progress));
            int heatMultiplier = accessor.getServerData().getInt("melter.multiplier");
            if(heatMultiplier > 0) {
                tooltip.add(new TranslatableComponent("melter.tooltip.multiplier", heatMultiplier));
                tooltip.add(new TextComponent("Heat source: " + accessor.getServerData().getString("melter.displayName")));
            } else {
                tooltip.add(new TranslatableComponent("melter.tooltip.multiplier_none"));
            }
        }

    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if(blockEntity instanceof MelterBlockEntity){
            MelterBlockEntity melter = (MelterBlockEntity) blockEntity;
            compoundTag.putInt("melter.progress",melter.getProgressPercent());
            compoundTag.putInt("melter.multiplier",melter.getHeatSourceMultiplier());
            compoundTag.putString("melter.displayName",melter.getHeatSourceDisplayName());
        }
    }
}
