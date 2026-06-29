package com.ikunkk02.crossbowarsenal.lockon;

public final class OverpoweredHomingRulesTest {
	private OverpoweredHomingRulesTest() {
	}

	public static void main(String[] args) {
		assertFalse(OverpoweredHomingRules.isThroughWallHomingActive(false, true), "arrows without shot authorization never gain wall homing");
		assertFalse(OverpoweredHomingRules.isThroughWallHomingActive(true, false), "server revocation disables wall homing");
		assertTrue(OverpoweredHomingRules.isThroughWallHomingActive(true, true), "shot authorization and current server permission enable wall homing");

		assertFalse(OverpoweredHomingRules.canBypassGuaranteedHitClearPath(true, false), "normal homing cannot bypass clear path");
		assertTrue(OverpoweredHomingRules.canBypassGuaranteedHitClearPath(false, false), "disabled clear-path requirement remains honored");
		assertTrue(OverpoweredHomingRules.canBypassGuaranteedHitClearPath(true, true), "authorized wall homing bypasses clear path");

	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertFalse(boolean condition, String message) {
		assertTrue(!condition, message);
	}
}
