package com.ikunkk02.crossbowarsenal.config;

public class CrossbowArsenalConfig {
	public boolean enableRepeatingCrossbow = true;
	public int repeatingDelayTicks = 4;
	public int level1Shots = 3;
	public int level2Shots = 5;
	public int level3Shots = 7;
	public double repeatingDamageMultiplier = 0.75D;
	public double repeatingSpreadMultiplier = 1.5D;
	public boolean enableLockOnSight = true;
	public boolean enableFullScreenLockOn = true;
	public double lockOnMaxDistance = 32.0D;
	public double lockOnAngleDegrees = 8.0D;
	public int lockOnScreenMargin = 20;
	public boolean requireLineOfSight = true;
	public double serverValidationFovDegrees = 120.0D;
	public int lockOnHomingTicks = 60;
	public double normalHomingStrength = 0.12D;
	public double undeadHomingStrength = 0.16D;
	public double bossHomingStrength = 0.08D;
	public double repeatingHomingMultiplier = 0.8D;
	public boolean showLockOnHud = true;
	public boolean showLockOnDebug = false;

	public int getShotsForLevel(int level) {
		return switch (level) {
			case 1 -> level1Shots;
			case 2 -> level2Shots;
			default -> level3Shots;
		};
	}

	public void sanitize() {
		repeatingDelayTicks = Math.max(1, repeatingDelayTicks);
		level1Shots = Math.max(1, level1Shots);
		level2Shots = Math.max(level1Shots, level2Shots);
		level3Shots = Math.max(level2Shots, level3Shots);
		repeatingDamageMultiplier = clamp(repeatingDamageMultiplier, 0.0D, 10.0D);
		repeatingSpreadMultiplier = clamp(repeatingSpreadMultiplier, 0.0D, 10.0D);
		lockOnMaxDistance = clamp(lockOnMaxDistance, 1.0D, 128.0D);
		lockOnAngleDegrees = clamp(lockOnAngleDegrees, 1.0D, 90.0D);
		lockOnScreenMargin = Math.max(0, Math.min(200, lockOnScreenMargin));
		serverValidationFovDegrees = sanitizePositive(serverValidationFovDegrees, 120.0D, 180.0D);
		lockOnHomingTicks = Math.max(1, lockOnHomingTicks);
		normalHomingStrength = clamp(normalHomingStrength, 0.0D, 1.0D);
		undeadHomingStrength = clamp(undeadHomingStrength, 0.0D, 1.0D);
		bossHomingStrength = clamp(bossHomingStrength, 0.0D, 1.0D);
		repeatingHomingMultiplier = clamp(repeatingHomingMultiplier, 0.0D, 1.0D);
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static double sanitizePositive(double value, double defaultValue, double max) {
		if (!Double.isFinite(value) || value <= 0.0D) {
			return defaultValue;
		}
		return Math.min(value, max);
	}
}
