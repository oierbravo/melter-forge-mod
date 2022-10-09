package com.oierbravo.melter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.oierbravo.melter.registrate.Config;
import com.oierbravo.melter.registrate.ModBlockEntities;
import com.oierbravo.melter.registrate.ModBlocks;
import com.oierbravo.melter.registrate.ModRecipes;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("melter")
public class Melter
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "melter";
    public static final String DISPLAY_NAME = "Melter";

    public static IEventBus modEventBus;

    public static final NonNullSupplier<Registrate> registrate = NonNullSupplier.lazy(() -> Registrate.create(MODID).creativeModeTab(ModCreativeModeTab::new, DISPLAY_NAME));

    public static final boolean withCreate = ModList.get().isLoaded("create");


    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    public Melter()
    {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.register();
        ModBlockEntities.register();
        ModRecipes.register(modEventBus);
        Config.register();

        registrate().addRawLang("melter.block.display", "Melter");
        registrate().addRawLang("melting.recipe", "Melting");
        registrate().addRawLang("melter.tooltip.progress", "Progress: %d%%");
        registrate().addRawLang("melter.tooltip.multiplier", "Heat multiplier: %d");
        registrate().addRawLang("melter.tooltip.multiplier_none", "§cNot heated!");
        registrate().addRawLang("config.jade.plugin_melter.melter_data", "Melter data");

    }


    public static Registrate registrate() {
        return registrate.get();
    }

    private static class ModCreativeModeTab extends CreativeModeTab {

        public ModCreativeModeTab() {
            super(DISPLAY_NAME);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.EGG);
        }
    }
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
