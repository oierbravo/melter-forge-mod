package com.oierbravo.melter.compat.jade;

import com.oierbravo.melter.content.melter.MelterBlock;
import com.oierbravo.melter.content.melter.MelterBlockEntity;
import mcp.mobius.waila.api.*;

@WailaPlugin
public class MelterPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new ProgressComponentProvider(), MelterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(new ProgressComponentProvider(), TooltipPosition.BODY, MelterBlock.class);
    }
}
