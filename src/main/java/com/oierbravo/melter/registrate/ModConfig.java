package com.oierbravo.melter.registrate;

import com.oierbravo.melter.content.melter.MelterConfig;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.ModConfigSpec;

//From https://github.com/McJty/TutorialV3/blob/1.19/src/main/java/com/example/tutorialv3/setup/Config.java
public class ModConfig {
    public static void register(ModContainer modContainer) {
        registerServerConfigs(modContainer);
    }
    private static void registerClientConfigs(ModContainer modContainer) {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }

    private static void registerCommonConfigs(ModContainer modContainer) {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

    private static void registerServerConfigs(ModContainer modContainer) {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        MelterConfig.registerServerConfig(SERVER_BUILDER);
        HeatSourcesConfig.registerServerConfig(SERVER_BUILDER);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }
}
