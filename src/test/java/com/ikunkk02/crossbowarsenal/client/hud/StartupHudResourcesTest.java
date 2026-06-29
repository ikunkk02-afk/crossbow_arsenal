package com.ikunkk02.crossbowarsenal.client.hud;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupHudResourcesTest {
	private StartupHudResourcesTest() {
	}

	public static void main(String[] args) throws IOException {
		String config = read("src/main/java/com/ikunkk02/crossbowarsenal/config/CrossbowArsenalConfig.java");
		assertContains(config, "public boolean enableStartupHud = true", "startup HUD default");
		assertContains(config, "public int startupHudDurationTicks = 30", "startup duration default");
		assertContains(config, "public double startupHudOpacity = 0.65D", "startup opacity default");
		assertContains(config, "public boolean enableStartupSound = true", "startup sound default");
		assertContains(config, "public boolean startupHudReplayOnSwitch = true", "startup replay default");

		String configScreen = read("src/client/java/com/ikunkk02/crossbowarsenal/client/config/CrossbowArsenalConfigScreen.java");
		for (String key : new String[]{
				"enableStartupHud", "startupHudDurationTicks", "startupHudOpacity",
				"enableStartupSound", "startupHudReplayOnSwitch"
		}) {
			assertContains(configScreen, "config.crossbow_arsenal." + key, "Cloth Config entry " + key);
		}

		String clientEntrypoint = read("src/client/java/com/ikunkk02/crossbowarsenal/client/Crossbow_arsenalClient.java");
		assertContains(clientEntrypoint, "LockOnStartupHudController.initialize()", "startup state registration");
		assertContains(clientEntrypoint, "StartupHudRenderer.initialize()", "startup renderer registration");

		String lockHud = read("src/client/java/com/ikunkk02/crossbowarsenal/client/hud/LockOnHudRenderer.java");
		assertContains(lockHud, "LockOnStartupHudController.isActive()", "normal HUD startup gate");

		String english = read("src/main/resources/assets/crossbow_arsenal/lang/en_us.json");
		String chinese = read("src/main/resources/assets/crossbow_arsenal/lang/zh_cn.json");
		for (String key : new String[]{
				"hud.crossbow_arsenal.system_booting", "hud.crossbow_arsenal.lock_on_sight_online",
				"hud.crossbow_arsenal.targeting_system_ready"
		}) {
			assertContains(english, key, "English startup HUD language");
			assertContains(chinese, key, "Chinese startup HUD language");
		}
	}

	private static String read(String path) throws IOException {
		Path file = Path.of(path);
		if (!Files.isRegularFile(file)) {
			throw new AssertionError("Missing file " + path);
		}
		return Files.readString(file);
	}

	private static void assertContains(String text, String expected, String message) {
		if (!text.contains(expected)) {
			throw new AssertionError(message + " must contain " + expected);
		}
	}
}
