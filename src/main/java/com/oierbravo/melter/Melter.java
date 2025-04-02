package com.oierbravo.melter;

import com.mojang.logging.LogUtils;
import com.oierbravo.melter.infrastructure.ModDataGen;
import com.oierbravo.melter.registrate.ModHeatSources;
import com.oierbravo.melter.content.melter.heatsource.HeatSourcesRegistry;
import com.oierbravo.melter.registrate.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("melter")
public class Melter
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "melter";

    public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MODID).defaultCreativeTab(ModCreativeTabs.MAIN_TAB.getKey()));

    public static final boolean withCreate = ModList.get().isLoaded("create");

    public Melter(IEventBus modEventBus, ModContainer modContainer)
    {

        ModBlocks.register();
        ModBlockEntities.register();
        ModCreativeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMessages.register();

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(ModMessages::registerNetworking);

        modEventBus.addListener(HeatSourcesRegistry::registerDatapackRegistries);
        modEventBus.addListener(ModHeatSources::onGatherData);
        modEventBus.addListener(ModDataGen::gatherData);

        ModConfig.register(modContainer);
        registrate().addRawLang("item.melter.melter.tooltip.summary", "A very special treat for your _Blaze Burners_. After eating this cake, Blaze Burners will _never run out of fuel_.");
        registrate().addRawLang("item.melter.melter.tooltip.condition1", "When Used");
        registrate().addRawLang("item.melter.melter.tooltip.behaviour1", "_Cycles_ a Blaze Burner's heat level.");
        registrate().addRawLang("itemGroup.melter:main", "Melter");
        registrate().addRawLang("melter.block.display", "Melter");
        registrate().addRawLang("melting.recipe", "Melting");
        registrate().addRawLang("heatsource.recipe", "Melter Heat Sources");
        registrate().addRawLang("creative_heat_source.tooltip", "Infinite heat level for the Melter");
        registrate().addRawLang("creative_heat_source.info", "Transforms any Melter into a 'Creative Melter' by placing this block under it. Creative Melter has an infinite heat level and will instantly melt anything. Also it won't hurt when you or any mob standing on it.");
        registrate().addRawLang("melter.tooltip.progress", "Progress: %d%%");
        registrate().addRawLang("melter.tooltip.heat_level", "Heat:");
        registrate().addRawLang("melter.tooltip.heat_level.creative", "§5infinite");
        registrate().addRawLang("melter.tooltip.heat_level.none", "§cNot heated");
        registrate().addRawLang("melter.tooltip.no_source_found", "No valid heat source found");
        registrate().addRawLang("melter.tooltip.creative", "Creative");
        registrate().addRawLang("melter.tooltip.create.blaze_burner.none", "None");
        registrate().addRawLang("melter.tooltip.create.blaze_burner.fading", "Fading");
        registrate().addRawLang("melter.tooltip.create.blaze_burner.smouldering", "Smouldering");
        registrate().addRawLang("melter.tooltip.create.blaze_burner.kindled", "Kindled");
        registrate().addRawLang("melter.tooltip.create.blaze_burner.seething", "Seething");
        registrate().addRawLang("config.jade.plugin_melter.melter_data", "Melter data");
        registrate().addRawLang("jei.melting.recipe.minimum_heat", "Minimum heat: %d");

    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MELTER_BLOCK_ENTITY.get(), (be, context) -> be.getFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MELTER_BLOCK_ENTITY.get(), (be, context) -> be.getItemHandler());
    }
    public static Registrate registrate() {
        return REGISTRATE.get();
    }


    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
