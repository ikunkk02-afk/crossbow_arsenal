package com.ikunkk02.crossbowarsenal.util;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;

public final class LockOnMathTest {
	private LockOnMathTest() {
	}

	public static void main(String[] args) {
		assertTrue(LockOnMath.isWithinForwardCone(1.0D, 120.0D), "straight ahead must pass");
		assertTrue(LockOnMath.isWithinForwardCone(0.5D, 120.0D), "half-angle edge must pass");
		assertFalse(LockOnMath.isWithinForwardCone(0.49D, 120.0D), "outside half-angle must fail");
		assertFalse(LockOnMath.isWithinForwardCone(0.0D, 120.0D), "side targets must fail server validation");

		assertDoubleEquals(0.0D, LockOnMath.clampHomingStrength(-1.0D), "negative homing strength clamps to zero");
		assertDoubleEquals(0.25D, LockOnMath.clampHomingStrength(0.25D), "normal homing strength stays unchanged");
		assertDoubleEquals(1.0D, LockOnMath.clampHomingStrength(2.0D), "large homing strength clamps to one");

		assertDoubleEquals(50.0D, LockOnMath.screenXFromNdc(-0.5D, 200), "negative NDC X must map to the left side of the screen");
		assertDoubleEquals(100.0D, LockOnMath.screenXFromNdc(0.0D, 200), "zero NDC X must map to screen center");
		assertDoubleEquals(150.0D, LockOnMath.screenXFromNdc(0.5D, 200), "positive NDC X must map to the right side of the screen");
		assertDoubleEquals(25.0D, LockOnMath.screenYFromNdc(0.5D, 100), "positive NDC Y must map toward the top of the screen");
		assertDoubleEquals(75.0D, LockOnMath.screenYFromNdc(-0.5D, 100), "negative NDC Y must map toward the bottom of the screen");

		CrossbowArsenalConfig config = new CrossbowArsenalConfig();
		config.serverValidationFovDegrees = 0.0D;
		config.sanitize();
		assertDoubleEquals(120.0D, config.serverValidationFovDegrees, "invalid server validation FOV must reset to the broad default");
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
