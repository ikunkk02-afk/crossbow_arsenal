package com.ikunkk02.crossbowarsenal.util;

import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;

public final class OverpoweredTargetingRulesTest {
	private OverpoweredTargetingRulesTest() {
	}

	public static void main(String[] args) {
		OverpoweredTargetingPolicy disabled = OverpoweredTargetingPolicy.disabled();
		assertEquals("player_targeting_disabled", OverpoweredTargetingRules.getRestrictionReason(disabled, true, false, false), "players require explicit permission");
		assertEquals("invisible_targeting_disabled", OverpoweredTargetingRules.getRestrictionReason(disabled, false, true, false), "invisible entities require explicit permission");
		assertEquals("through_wall_targeting_disabled", OverpoweredTargetingRules.getRestrictionReason(disabled, false, false, true), "through-wall targets require explicit permission");

		OverpoweredTargetingPolicy enabled = new OverpoweredTargetingPolicy(true, true, true, false, true, 64.0D);
		assertEquals(null, OverpoweredTargetingRules.getRestrictionReason(enabled, true, true, true), "enabled policy permits combined exceptions");

		assertTrue(OverpoweredTargetingRules.getPriority(true, false, false, false) < OverpoweredTargetingRules.getPriority(false, true, false, false), "players outrank bosses");
		assertTrue(OverpoweredTargetingRules.getPriority(false, true, false, false) < OverpoweredTargetingRules.getPriority(false, false, true, false), "bosses outrank undead");
		assertTrue(OverpoweredTargetingRules.getPriority(false, false, true, false) < OverpoweredTargetingRules.getPriority(false, false, false, true), "undead outrank hostile mobs");
		assertTrue(OverpoweredTargetingRules.getPriority(false, false, false, true) < OverpoweredTargetingRules.getPriority(false, false, false, false), "hostile mobs outrank other mobs");
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!java.util.Objects.equals(expected, actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
