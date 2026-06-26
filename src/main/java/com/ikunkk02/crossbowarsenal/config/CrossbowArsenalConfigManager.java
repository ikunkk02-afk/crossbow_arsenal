package com.ikunkk02.crossbowarsenal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CrossbowArsenalConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("crossbow_arsenal.json");
	private static CrossbowArsenalConfig config = new CrossbowArsenalConfig();

	private CrossbowArsenalConfigManager() {
	}

	public static CrossbowArsenalConfig getConfig() {
		return config;
	}

	public static void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				CrossbowArsenalConfig loaded = GSON.fromJson(reader, CrossbowArsenalConfig.class);
				config = loaded == null ? new CrossbowArsenalConfig() : loaded;
			} catch (IOException | RuntimeException exception) {
				Crossbow_arsenal.LOGGER.warn("Failed to load Crossbow Arsenal config, using defaults", exception);
				config = new CrossbowArsenalConfig();
			}
		}

		config.sanitize();
		save();
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException exception) {
			Crossbow_arsenal.LOGGER.warn("Failed to save Crossbow Arsenal config", exception);
		}
	}
}
