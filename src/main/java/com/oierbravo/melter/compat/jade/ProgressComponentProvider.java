package com.oierbravo.melter.compat.jade;

import com.oierbravo.melter.content.melter.MelterBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;

public class ProgressComponentProvider  implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        //CompoundTag serverData = accessor.getServerData();
        if (accessor.getServerData().contains("melter.progress")) {
            int progress = accessor.getServerData().getInt("melter.progress");
            IElementHelper elementHelper = tooltip.getElementHelper();
            IProgressStyle progressStyle = elementHelper.progressStyle();
            if(progress > 0)
                tooltip.add(elementHelper.progress((float)progress / 100, Component.translatable("melter.tooltip.progress", progress), progressStyle,elementHelper.borderStyle()));
            int heatMultiplier = accessor.getServerData().getInt("melter.multiplier");
            if(heatMultiplier > 0) {
                tooltip.add(Component.translatable("melter.tooltip.oneline",accessor.getServerData().getString("melter.displayName"), heatMultiplier));
            } else {
                tooltip.add(Component.translatable("melter.tooltip.multiplier_none"));
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

    @Override
    public ResourceLocation getUid() {
        return MelterPlugin.MELTER_DATA;
    }
}
