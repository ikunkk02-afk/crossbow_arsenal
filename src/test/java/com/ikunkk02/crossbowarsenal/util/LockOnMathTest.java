package com.ikunkk02.crossbowarsenal.util;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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

		Vec3d currentVelocity = new Vec3d(3.0D, 0.0D, 0.0D);
		Vec3d desiredDirection = new Vec3d(0.0D, 1.0D, 0.0D);
		Vec3d adjustedVelocity = LockOnMath.steerVelocity(currentVelocity, desiredDirection, 0.25D);
		assertDoubleEquals(currentVelocity.length(), adjustedVelocity.length(), 1.0E-9D, "homing must preserve current speed");
		assertTrue(adjustedVelocity.x > 0.0D && adjustedVelocity.y > 0.0D, "partial homing must turn toward the target");
		assertVecEquals(currentVelocity, LockOnMath.steerVelocity(currentVelocity, desiredDirection, 0.0D), "zero strength must keep current velocity");
		assertVecEquals(new Vec3d(0.0D, 3.0D, 0.0D), LockOnMath.steerVelocity(currentVelocity, desiredDirection, 1.0D), "full strength must face the target while preserving speed");
		assertVecEquals(Vec3d.ZERO, LockOnMath.steerVelocity(Vec3d.ZERO, desiredDirection, 0.25D), "zero velocity must remain stopped");
		assertVecEquals(currentVelocity, LockOnMath.steerVelocity(currentVelocity, Vec3d.ZERO, 0.25D), "invalid desired direction must keep current velocity");

		Vec3d terminalVelocity = LockOnMath.steerTerminalVelocity(
			new Vec3d(0.4D, 0.0D, 0.0D),
			desiredDirection,
			1.0D,
			1.5D
		);
		assertVecEquals(new Vec3d(0.0D, 1.5D, 0.0D), terminalVelocity, 1.0E-9D, "terminal homing must face the target and enforce minimum speed");
		Vec3d strongTerminalVelocity = LockOnMath.steerTerminalVelocity(currentVelocity, desiredDirection, 0.9D, 1.5D);
		assertDoubleEquals(3.0D, strongTerminalVelocity.length(), 1.0E-9D, "terminal homing must not increase an already fast arrow speed");
		assertTrue(strongTerminalVelocity.normalize().dotProduct(desiredDirection) > 0.99D, "terminal homing must turn almost directly toward the target");

		assertDoubleEquals(1.0D, LockOnMath.calculatePredictionTime(3.0D, 3.0D), "prediction time must use distance divided by arrow speed");
		assertDoubleEquals(1.2D, LockOnMath.calculatePredictionTime(12.0D, 3.0D), "prediction time must cap at 1.2 seconds");
		assertDoubleEquals(0.0D, LockOnMath.calculatePredictionTime(-1.0D, 3.0D), "negative distance must not predict backwards");
		assertDoubleEquals(0.0D, LockOnMath.calculatePredictionTime(3.0D, 0.0D), "stopped arrows must not create invalid prediction times");

		Vec3d predictedAimPoint = LockOnMath.predictAimPoint(
			Vec3d.ZERO,
			new Vec3d(6.0D, 0.0D, 0.0D),
			new Vec3d(0.0D, 0.0D, 1.0D),
			3.0D,
			0.015D
		);
		assertVecEquals(new Vec3d(6.0D, 0.09D, 1.2D), predictedAimPoint, 1.0E-9D, "prediction must lead moving targets and compensate arrow drop");

		Box expandedTargetBox = new Box(-0.5D, 0.0D, -0.5D, 0.5D, 2.0D, 0.5D).expand(0.75D);
		assertTrue(LockOnMath.getBoxIntersection(expandedTargetBox, new Vec3d(-4.0D, 1.0D, 0.0D), new Vec3d(4.0D, 1.0D, 0.0D)).isPresent(), "a fast arrow segment crossing the expanded target box must hit");
		assertFalse(LockOnMath.getBoxIntersection(expandedTargetBox, new Vec3d(-4.0D, 3.0D, 0.0D), new Vec3d(4.0D, 3.0D, 0.0D)).isPresent(), "a segment above the expanded target box must miss");
		Vec3d insideBox = new Vec3d(0.0D, 1.0D, 0.0D);
		assertVecEquals(insideBox, LockOnMath.getBoxIntersection(expandedTargetBox, insideBox, new Vec3d(4.0D, 1.0D, 0.0D)).orElseThrow(), "a segment starting inside the expanded target box must hit immediately");

		assertDoubleEquals(50.0D, LockOnMath.screenXFromNdc(-0.5D, 200), "negative NDC X must map to the left side of the screen");
		assertDoubleEquals(100.0D, LockOnMath.screenXFromNdc(0.0D, 200), "zero NDC X must map to screen center");
		assertDoubleEquals(150.0D, LockOnMath.screenXFromNdc(0.5D, 200), "positive NDC X must map to the right side of the screen");
		assertDoubleEquals(25.0D, LockOnMath.screenYFromNdc(0.5D, 100), "positive NDC Y must map toward the top of the screen");
		assertDoubleEquals(75.0D, LockOnMath.screenYFromNdc(-0.5D, 100), "negative NDC Y must map toward the bottom of the screen");

		CrossbowArsenalConfig config = new CrossbowArsenalConfig();
		assertIntEquals(120, config.lockOnHomingTicks, "homing test duration default");
		assertDoubleEquals(0.55D, config.normalHomingStrength, "normal homing test strength default");
		assertDoubleEquals(0.70D, config.undeadHomingStrength, "undead homing test strength default");
		assertDoubleEquals(0.28D, config.bossHomingStrength, "boss homing test strength default");
		assertDoubleEquals(0.015D, config.homingGravityCompensation, "gravity compensation test default");
		assertTrue(config.enableGuaranteedHomingHit, "guaranteed homing hit must default to enabled");
		assertDoubleEquals(4.0D, config.terminalHomingRadius, "terminal homing radius default");
		assertDoubleEquals(0.9D, config.terminalHomingStrength, "terminal homing strength default");
		assertDoubleEquals(0.75D, config.homingHitboxExpansion, "homing hitbox expansion default");
		assertTrue(config.requireClearPathForGuaranteedHit, "guaranteed hit clear-path gate must default to enabled");

		CrossbowArsenalConfig legacyConfig = new CrossbowArsenalConfig();
		legacyConfig.normalHomingStrength = 0.45D;
		legacyConfig.undeadHomingStrength = 0.55D;
		legacyConfig.bossHomingStrength = 0.22D;
		legacyConfig.migrateLegacyHomingDefaults();
		assertDoubleEquals(0.55D, legacyConfig.normalHomingStrength, "legacy normal homing default must migrate");
		assertDoubleEquals(0.70D, legacyConfig.undeadHomingStrength, "legacy undead homing default must migrate");
		assertDoubleEquals(0.28D, legacyConfig.bossHomingStrength, "legacy boss homing default must migrate");

		CrossbowArsenalConfig oldestLegacyConfig = new CrossbowArsenalConfig();
		oldestLegacyConfig.lockOnHomingTicks = 60;
		oldestLegacyConfig.normalHomingStrength = 0.12D;
		oldestLegacyConfig.undeadHomingStrength = 0.16D;
		oldestLegacyConfig.bossHomingStrength = 0.08D;
		oldestLegacyConfig.migrateLegacyHomingDefaults();
		assertIntEquals(120, oldestLegacyConfig.lockOnHomingTicks, "oldest legacy homing duration must migrate");
		assertDoubleEquals(0.55D, oldestLegacyConfig.normalHomingStrength, "oldest legacy normal homing default must migrate");
		assertDoubleEquals(0.70D, oldestLegacyConfig.undeadHomingStrength, "oldest legacy undead homing default must migrate");
		assertDoubleEquals(0.28D, oldestLegacyConfig.bossHomingStrength, "oldest legacy boss homing default must migrate");

		CrossbowArsenalConfig customizedLegacyConfig = new CrossbowArsenalConfig();
		customizedLegacyConfig.normalHomingStrength = 0.60D;
		customizedLegacyConfig.undeadHomingStrength = 0.80D;
		customizedLegacyConfig.bossHomingStrength = 0.30D;
		customizedLegacyConfig.migrateLegacyHomingDefaults();
		assertDoubleEquals(0.60D, customizedLegacyConfig.normalHomingStrength, "custom normal homing strength must not be overwritten");
		assertDoubleEquals(0.80D, customizedLegacyConfig.undeadHomingStrength, "custom undead homing strength must not be overwritten");
		assertDoubleEquals(0.30D, customizedLegacyConfig.bossHomingStrength, "custom boss homing strength must not be overwritten");
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
		assertDoubleEquals(expected, actual, 0.0D, message);
	}

	private static void assertDoubleEquals(double expected, double actual, double tolerance, String message) {
		if (Math.abs(expected - actual) > tolerance) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}

	private static void assertIntEquals(int expected, int actual, String message) {
		if (expected != actual) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}

	private static void assertVecEquals(Vec3d expected, Vec3d actual, String message) {
		if (!expected.equals(actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}

	private static void assertVecEquals(Vec3d expected, Vec3d actual, double tolerance, String message) {
		assertDoubleEquals(expected.x, actual.x, tolerance, message + " (x)");
		assertDoubleEquals(expected.y, actual.y, tolerance, message + " (y)");
		assertDoubleEquals(expected.z, actual.z, tolerance, message + " (z)");
	}

}
