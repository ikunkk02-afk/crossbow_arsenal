package com.ikunkk02.crossbowarsenal.config;

public final class OverpoweredTargetingPolicyTest {
	private OverpoweredTargetingPolicyTest() {
	}

	public static void main(String[] args) {
		CrossbowArsenalConfig defaults = new CrossbowArsenalConfig();
		assertFalse(defaults.enableOverpoweredTargeting, "overpowered master switch defaults off");
		assertFalse(defaults.allowTargetPlayers, "player targeting defaults off");
		assertFalse(defaults.allowTargetThroughWalls, "through-wall targeting defaults off");
		assertFalse(defaults.allowHomingThroughWalls, "through-wall homing defaults off");
		assertFalse(defaults.allowTargetInvisibleEntities, "invisible targeting defaults off");
		assertDoubleEquals(64.0D, defaults.overpoweredTargetMaxDistance, "overpowered distance default");
		assertTrue(defaults.showOverpoweredWarning, "overpowered warning defaults on");

		OverpoweredTargetingPolicy disabled = OverpoweredTargetingPolicy.disabled();
		assertFalse(disabled.enabled(), "disabled policy is not enabled");
		assertDoubleEquals(32.0D, disabled.targetMaxDistance(32.0D), "disabled policy keeps normal distance");

		OverpoweredTargetingPolicy local = new OverpoweredTargetingPolicy(true, true, true, true, true, 96.0D);
		OverpoweredTargetingPolicy server = new OverpoweredTargetingPolicy(true, true, false, true, false, 64.0D);
		OverpoweredTargetingPolicy effective = local.intersect(server);
		assertTrue(effective.enabled(), "both master switches enable policy");
		assertTrue(effective.allowTargetPlayers(), "both player flags enable player targeting");
		assertFalse(effective.allowTargetThroughWalls(), "server false blocks through-wall targeting");
		assertTrue(effective.allowHomingThroughWalls(), "both homing flags enable through-wall homing");
		assertFalse(effective.allowTargetInvisibleEntities(), "server false blocks invisible targeting");
		assertDoubleEquals(64.0D, effective.overpoweredTargetMaxDistance(), "effective distance uses stricter limit");
		assertDoubleEquals(64.0D, effective.targetMaxDistance(32.0D), "enabled policy replaces normal distance");

		OverpoweredTargetingPolicy clientDisabled = OverpoweredTargetingPolicy.disabled().intersect(server);
		assertFalse(clientDisabled.enabled(), "client opt-out keeps effective policy disabled");
		assertFalse(clientDisabled.allowTargetPlayers(), "disabled master switch disables child flags");

		CrossbowArsenalConfig invalidDistance = new CrossbowArsenalConfig();
		invalidDistance.overpoweredTargetMaxDistance = 500.0D;
		invalidDistance.sanitize();
		assertDoubleEquals(128.0D, invalidDistance.overpoweredTargetMaxDistance, "overpowered distance clamps to configured maximum");
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertFalse(boolean condition, String message) {
		assertTrue(!condition, message);
	}

	private static void assertDoubleEquals(double expected, double actual, String message) {
		if (Double.compare(expected, actual) != 0) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
