package com.oierbravo.melter.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigCommon {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MELTER_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> MELTER_FLUID_PER_TICK;

    static {
        BUILDER.push("Configs for WaterCondenser");

        MELTER_CAPACITY = BUILDER.comment("Tank capacity in mB")
                .define("Condenser capacity", 1000);
        MELTER_FLUID_PER_TICK = BUILDER.comment("mB per tick increment")
                .define("Fluid per tick", 2);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
