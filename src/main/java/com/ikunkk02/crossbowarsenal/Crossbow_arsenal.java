package com.ikunkk02.crossbowarsenal;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.enchantment.ModEnchantments;
import com.ikunkk02.crossbowarsenal.item.ModItems;
import com.ikunkk02.crossbowarsenal.lockon.LockOnManager;
import com.ikunkk02.crossbowarsenal.network.ModNetworking;
import com.ikunkk02.crossbowarsenal.recipe.ModRecipes;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingCrossbowManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crossbow_arsenal implements ModInitializer {
	public static final String MOD_ID = "crossbow_arsenal";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CrossbowArsenalConfigManager.load();
		ModItems.initialize();
		ModRecipes.initialize();
		ModEnchantments.initialize();
		ModNetworking.initialize();
		RepeatingCrossbowManager.initialize();
		LockOnManager.initialize();
		LOGGER.info("Initialized Crossbow Arsenal");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
