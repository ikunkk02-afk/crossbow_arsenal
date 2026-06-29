package com.ikunkk02.crossbowarsenal.config;

public class CrossbowArsenalConfig {
	private static final int CURRENT_HOMING_CONFIG_VERSION = 1;

	int homingConfigVersion = CURRENT_HOMING_CONFIG_VERSION;
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
	public int lockOnHomingTicks = 120;
	public double normalHomingStrength = 0.55D;
	public double undeadHomingStrength = 0.70D;
	public double bossHomingStrength = 0.28D;
	public double homingGravityCompensation = 0.015D;
	public boolean enableGuaranteedHomingHit = true;
	public double terminalHomingRadius = 4.0D;
	public double terminalHomingStrength = 0.9D;
	public double homingHitboxExpansion = 0.75D;
	public boolean requireClearPathForGuaranteedHit = true;
	public double repeatingHomingMultiplier = 0.8D;
	public boolean showLockOnHud = true;
	public boolean showLockOnDebug = false;
	public boolean enableOverpoweredTargeting = false;
	public boolean allowTargetPlayers = false;
	public boolean allowTargetThroughWalls = false;
	public boolean allowHomingThroughWalls = false;
	public boolean allowTargetInvisibleEntities = false;
	public double overpoweredTargetMaxDistance = 64.0D;
	public boolean overpoweredPenetrationBreaksStone = true;
	public boolean overpoweredPenetrationBreaksWood = true;
	public int maxOverpoweredBlocksPenetrated = 8;
	public double overpoweredHardBlockSpeedMultiplier = 0.75D;
	public double overpoweredHardBlockDamageMultiplier = 0.75D;
	public boolean showOverpoweredWarning = true;
	public boolean enableStartupHud = true;
	public int startupHudDurationTicks = 30;
	public double startupHudOpacity = 0.65D;
	public boolean enableStartupSound = true;
	public boolean startupHudReplayOnSwitch = true;
	public boolean enableGlassPenetration = true;
	public double glassPenetrationDamageMultiplier = 0.9D;
	public double glassPenetrationSpeedMultiplier = 0.92D;
	public boolean glassPenetrationConsumesDurability = false;
	public boolean fragileBlockPenetrationEnabled = true;
	public double fragileBlockDamageMultiplier = 0.8D;
	public double fragileBlockSpeedMultiplier = 0.85D;
	public int maxFragileBlocksPenetrated = 3;
	public boolean entityPenetrationEnabled = true;
	public double entityPenetrationDamageDecay = 0.8D;
	public int maxEntityPenetrations = 3;
	public boolean lockOnArrowCanPenetrateGlass = true;
	public boolean lockOnArrowCanPenetrateFragileBlocks = true;
	public boolean showPenetrationDebug = false;
	public boolean enablePenetratingArrow = true;
	public boolean enableExplosiveArrow = true;
	public boolean penetratingArrowCanPenetrateFragileBlocks = true;
	public int penetratingArrowSoftBlockRequiresPiercingLevel = 2;
	public int penetratingArrowWoodBlockRequiresPiercingLevel = 3;
	public double penetratingArrowDamageMultiplierPerBlock = 0.85D;
	public double penetratingArrowSpeedMultiplierPerBlock = 0.9D;
	public double explosiveArrowBasePower = 2.0D;
	public double explosiveEnchantLevel1Power = 1.5D;
	public double explosiveEnchantLevel2Power = 2.2D;
	public double explosiveEnchantLevel3Power = 3.0D;
	public double maxExplosionPower = 4.0D;
	public boolean explosiveArrowBreakBlocks = false;
	public boolean explosiveEnchantBreakBlocks = false;
	public boolean explosiveArrowFire = false;
	public double explosiveArrowKnockbackMultiplier = 1.0D;
	public double explosiveArrowSelfDamageMultiplier = 0.35D;
	public boolean explosiveStopsPenetration = true;

	public int getShotsForLevel(int level) {
		return switch (level) {
			case 1 -> level1Shots;
			case 2 -> level2Shots;
			default -> level3Shots;
		};
	}

	public void migrateLegacyHomingDefaults() {
		if (lockOnHomingTicks == 60) {
			lockOnHomingTicks = 120;
		}
		if (Double.compare(normalHomingStrength, 0.12D) == 0 || Double.compare(normalHomingStrength, 0.45D) == 0) {
			normalHomingStrength = 0.55D;
		}
		if (Double.compare(undeadHomingStrength, 0.16D) == 0 || Double.compare(undeadHomingStrength, 0.55D) == 0) {
			undeadHomingStrength = 0.70D;
		}
		if (Double.compare(bossHomingStrength, 0.08D) == 0 || Double.compare(bossHomingStrength, 0.22D) == 0) {
			bossHomingStrength = 0.28D;
		}
		homingConfigVersion = CURRENT_HOMING_CONFIG_VERSION;
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
		homingGravityCompensation = clamp(homingGravityCompensation, 0.0D, 0.1D);
		terminalHomingRadius = sanitizeRange(terminalHomingRadius, 4.0D, 0.1D, 128.0D);
		terminalHomingStrength = sanitizeRange(terminalHomingStrength, 0.9D, 0.0D, 1.0D);
		homingHitboxExpansion = sanitizeRange(homingHitboxExpansion, 0.75D, 0.0D, 4.0D);
		repeatingHomingMultiplier = clamp(repeatingHomingMultiplier, 0.0D, 1.0D);
		overpoweredTargetMaxDistance = sanitizeRange(overpoweredTargetMaxDistance, 64.0D, 1.0D, 128.0D);
		maxOverpoweredBlocksPenetrated = Math.max(0, Math.min(64, maxOverpoweredBlocksPenetrated));
		overpoweredHardBlockSpeedMultiplier = clamp(overpoweredHardBlockSpeedMultiplier, 0.0D, 1.0D);
		overpoweredHardBlockDamageMultiplier = clamp(overpoweredHardBlockDamageMultiplier, 0.0D, 1.0D);
		startupHudDurationTicks = Math.max(1, Math.min(600, startupHudDurationTicks));
		startupHudOpacity = sanitizeRange(startupHudOpacity, 0.65D, 0.0D, 1.0D);
		glassPenetrationDamageMultiplier = clamp(glassPenetrationDamageMultiplier, 0.0D, 1.0D);
		glassPenetrationSpeedMultiplier = clamp(glassPenetrationSpeedMultiplier, 0.0D, 1.0D);
		fragileBlockDamageMultiplier = clamp(fragileBlockDamageMultiplier, 0.0D, 1.0D);
		fragileBlockSpeedMultiplier = clamp(fragileBlockSpeedMultiplier, 0.0D, 1.0D);
		maxFragileBlocksPenetrated = Math.max(0, Math.min(64, maxFragileBlocksPenetrated));
		entityPenetrationDamageDecay = clamp(entityPenetrationDamageDecay, 0.0D, 1.0D);
		maxEntityPenetrations = Math.max(1, Math.min(64, maxEntityPenetrations));
		penetratingArrowSoftBlockRequiresPiercingLevel = Math.max(0, Math.min(255, penetratingArrowSoftBlockRequiresPiercingLevel));
		penetratingArrowWoodBlockRequiresPiercingLevel = Math.max(0, Math.min(255, penetratingArrowWoodBlockRequiresPiercingLevel));
		penetratingArrowDamageMultiplierPerBlock = clamp(penetratingArrowDamageMultiplierPerBlock, 0.0D, 1.0D);
		penetratingArrowSpeedMultiplierPerBlock = clamp(penetratingArrowSpeedMultiplierPerBlock, 0.0D, 1.0D);
		explosiveArrowBasePower = sanitizeRange(explosiveArrowBasePower, 2.0D, 0.0D, 16.0D);
		explosiveEnchantLevel1Power = sanitizeRange(explosiveEnchantLevel1Power, 1.5D, 0.0D, 16.0D);
		explosiveEnchantLevel2Power = sanitizeRange(explosiveEnchantLevel2Power, 2.2D, 0.0D, 16.0D);
		explosiveEnchantLevel3Power = sanitizeRange(explosiveEnchantLevel3Power, 3.0D, 0.0D, 16.0D);
		maxExplosionPower = sanitizeRange(maxExplosionPower, 4.0D, 0.0D, 16.0D);
		explosiveArrowKnockbackMultiplier = sanitizeRange(explosiveArrowKnockbackMultiplier, 1.0D, 0.0D, 10.0D);
		explosiveArrowSelfDamageMultiplier = sanitizeRange(explosiveArrowSelfDamageMultiplier, 0.35D, 0.0D, 10.0D);
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

	private static double sanitizeRange(double value, double defaultValue, double min, double max) {
		if (!Double.isFinite(value)) {
			return defaultValue;
		}
		return clamp(value, min, max);
	}
}
