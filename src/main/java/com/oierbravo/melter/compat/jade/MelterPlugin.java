package com.oierbravo.melter.compat.jade;

import com.oierbravo.melter.content.melter.MelterBlock;
import com.oierbravo.melter.content.melter.MelterBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;

@WailaPlugin
public class MelterPlugin implements IWailaPlugin {
    public static final ResourceLocation MELTER_DATA = new ResourceLocation("melter:melter_data");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new ProgressComponentProvider(), MelterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new ProgressComponentProvider(), MelterBlock.class);
    }
}
