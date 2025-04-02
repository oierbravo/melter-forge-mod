package com.oierbravo.melter.foundation.utility;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public final class RegisteredObjects {
	// registry argument for easier porting to 1.19
	@NotNull
	public static <V> ResourceLocation getKeyOrThrow(Registry<V> registry, V value) {
		ResourceLocation key = registry.getKey(value);
		if (key == null) {
			throw new IllegalArgumentException("Could not get key for value " + value + "!");
		}
		return key;
	}

	@NotNull
	public static ResourceLocation getKeyOrThrow(Fluid value) {
		return getKeyOrThrow(BuiltInRegistries.FLUID, value);
	}

}
