package com.oierbravo.melter.registrate;

import com.oierbravo.melter.Melter;
import com.oierbravo.melter.content.melter.MelterBlockEntity;
import com.oierbravo.melter.content.melter.MelterBlockRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<MelterBlockEntity> MELTER_BLOCK_ENTITY = Melter.registrate()
            //.blockEntity(TradingStationBlockEntity::new)
            .blockEntity("melter_block_entity", MelterBlockEntity::new)
            .validBlocks(ModBlocks.MELTER)
            .renderer(() -> MelterBlockRenderer::new)
            .register();
    public static void register() {

    }
}
