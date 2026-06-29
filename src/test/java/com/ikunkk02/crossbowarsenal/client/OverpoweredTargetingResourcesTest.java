package com.ikunkk02.crossbowarsenal.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OverpoweredTargetingResourcesTest {
	private static final String[] CONFIG_KEYS = {
			"enableOverpoweredTargeting", "allowTargetPlayers", "allowTargetThroughWalls",
			"allowHomingThroughWalls", "allowTargetInvisibleEntities",
			"overpoweredTargetMaxDistance", "overpoweredPenetrationBreaksStone",
			"overpoweredPenetrationBreaksWood", "maxOverpoweredBlocksPenetrated",
			"overpoweredHardBlockSpeedMultiplier", "overpoweredHardBlockDamageMultiplier",
			"showOverpoweredWarning"
	};

	private OverpoweredTargetingResourcesTest() {
	}

	public static void main(String[] args) throws IOException {
		String configScreen = read("src/client/java/com/ikunkk02/crossbowarsenal/client/config/CrossbowArsenalConfigScreen.java");
		assertContains(configScreen, "startTextDescription", "red warning description");
		assertContains(configScreen, "Formatting.RED", "red warning color");
		assertContains(configScreen, "ConfirmScreen", "explicit enable confirmation");
		assertContains(configScreen, "ClientTargetingNetworking.requestPolicyRefresh()", "policy refresh after config save");
		for (String key : CONFIG_KEYS) {
			assertContains(configScreen, "config.crossbow_arsenal." + key, "Cloth Config entry " + key);
		}
		assertNotContains(configScreen, "startBooleanToggle(Text.translatable(\"config.crossbow_arsenal.requireLineOfSight\")", "legacy LOS toggle must not remain editable");

		String clientEntrypoint = read("src/client/java/com/ikunkk02/crossbowarsenal/client/Crossbow_arsenalClient.java");
		assertContains(clientEntrypoint, "OverpoweredWarningController.initialize()", "warning controller registration");
		assertContains(clientEntrypoint, "OverpoweredWarningRenderer.initialize()", "warning renderer registration");

		String lockHud = read("src/client/java/com/ikunkk02/crossbowarsenal/client/hud/LockOnHudRenderer.java");
		assertContains(lockHud, "hud.crossbow_arsenal.through_wall", "through-wall HUD label");
		assertContains(lockHud, "ClientTargetingPolicyState.getLastRejectedReason()", "server rejection debug line");

		String homingInterface = read("src/main/java/com/ikunkk02/crossbowarsenal/lockon/HomingProjectile.java");
		assertContains(homingInterface, "boolean homingThroughWallsAuthorized", "server-stamped homing authorization parameter");
		String projectileMixin = read("src/main/java/com/ikunkk02/crossbowarsenal/mixin/PersistentProjectileEntityMixin.java");
		assertContains(projectileMixin, "CrossbowArsenalHomingThroughWalls", "homing authorization NBT key");
		assertContains(projectileMixin, "ModBlockTags.OVERPOWERED_PENETRABLE_BLOCKS", "synchronized wall destruction allow-list");
		assertContains(projectileMixin, "recordOverpoweredBlockPenetration", "overpowered penetration budget integration");
		assertContains(projectileMixin, "serverWorld.breakBlock", "server-authoritative block destruction");
		assertNotContains(projectileMixin, "projectile.setNoClip(true)", "unbroken blocks must not be phased");

		String english = read("src/main/resources/assets/crossbow_arsenal/lang/en_us.json");
		String chinese = read("src/main/resources/assets/crossbow_arsenal/lang/zh_cn.json");
		for (String key : CONFIG_KEYS) {
			assertContains(english, "config.crossbow_arsenal." + key, "English config language " + key);
			assertContains(chinese, "config.crossbow_arsenal." + key, "Chinese config language " + key);
		}
		for (String key : new String[]{
				"config.crossbow_arsenal.overpowered.warning",
				"config.crossbow_arsenal.overpowered.confirm",
				"hud.crossbow_arsenal.overpowered_enabled",
				"hud.crossbow_arsenal.overpowered_detail",
				"hud.crossbow_arsenal.through_wall"
		}) {
			assertContains(english, key, "English warning language " + key);
			assertContains(chinese, key, "Chinese warning language " + key);
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

	private static void assertNotContains(String text, String unexpected, String message) {
		if (text.contains(unexpected)) {
			throw new AssertionError(message + " must not contain " + unexpected);
		}
	}
}
