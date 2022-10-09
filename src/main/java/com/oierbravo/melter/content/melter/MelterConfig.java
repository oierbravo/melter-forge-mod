package com.oierbravo.melter.content.melter;

import net.minecraftforge.common.ForgeConfigSpec;

public class MelterConfig {
    public static ForgeConfigSpec.IntValue MELTER_CAPACITY;
    public static ForgeConfigSpec.IntValue MELTER_FLUID_PER_TICK;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the melter").push("melter");
        MELTER_CAPACITY = SERVER_BUILDER
                .comment("How much liquid fits into the melter, in mB")
                .defineInRange("capacity", 1000, 1, Integer.MAX_VALUE);
        MELTER_FLUID_PER_TICK = SERVER_BUILDER
                .comment("How much liquid generates per tick, in mB")
                .defineInRange("capacity", 2, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
    }
}
