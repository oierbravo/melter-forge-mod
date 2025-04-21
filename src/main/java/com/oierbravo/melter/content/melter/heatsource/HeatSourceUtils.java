package com.oierbravo.melter.content.melter.heatsource;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class HeatSourceUtils {
    public static ItemStack generateItemStackWithCustomItemName(ItemStack itemStack, MutableComponent component){
        itemStack.set(DataComponents.ITEM_NAME,component);
        return itemStack;
    }
}
