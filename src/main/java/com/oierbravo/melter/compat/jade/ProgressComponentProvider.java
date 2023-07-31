package com.oierbravo.melter.compat.jade;

import com.oierbravo.melter.content.melter.MelterBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;

public class ProgressComponentProvider  implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        //CompoundTag serverData = accessor.getServerData();
        if (accessor.getServerData().contains("melter.progress")) {
            int progress = accessor.getServerData().getInt("melter.progress");
            IElementHelper elementHelper = IElementHelper.get();
            IProgressStyle progressStyle = elementHelper.progressStyle();
            if(progress > 0)
                tooltip.add(elementHelper.progress((float)progress / 100, Component.translatable("melter.tooltip.progress", progress), progressStyle, BoxStyle.DEFAULT, true));
            int heatMultiplier = accessor.getServerData().getInt("melter.multiplier");
            if(heatMultiplier > 0) {
                tooltip.add(Component.translatable("melter.tooltip.oneline",accessor.getServerData().getString("melter.displayName"), heatMultiplier));
            } else {
                tooltip.add(Component.translatable("melter.tooltip.multiplier_none"));
            }
        }

    }

    @Override
    public ResourceLocation getUid() {
        return MelterPlugin.MELTER_DATA;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        MelterBlockEntity blockEntity = (MelterBlockEntity) blockAccessor.getBlockEntity();

        if(blockEntity != null){
            MelterBlockEntity melter = (MelterBlockEntity) blockEntity;
            compoundTag.putInt("melter.progress",melter.getProgressPercent());
            compoundTag.putInt("melter.multiplier",melter.getHeatSourceMultiplier());
            compoundTag.putString("melter.displayName",melter.getHeatSourceDisplayName());
        }
    }
}
