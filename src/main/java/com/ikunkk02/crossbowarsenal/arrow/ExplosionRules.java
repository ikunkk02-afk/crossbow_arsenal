package com.ikunkk02.crossbowarsenal.arrow;

import net.minecraft.util.math.Vec3d;

public final class ExplosionRules {
	private ExplosionRules() {
	}

	public static double calculatePower(
			ArrowType arrowType,
			boolean explosiveArrowEnabled,
			int explosiveLevel,
			double basePower,
			double levelOnePower,
			double levelTwoPower,
			double levelThreePower,
			double maxPower
	) {
		double power = arrowType == ArrowType.EXPLOSIVE && explosiveArrowEnabled ? sanitizeNonNegative(basePower) : 0.0D;
		power += switch (explosiveLevel) {
			case 1 -> sanitizeNonNegative(levelOnePower);
			case 2 -> sanitizeNonNegative(levelTwoPower);
			default -> explosiveLevel >= 3 ? sanitizeNonNegative(levelThreePower) : 0.0D;
		};
		return Math.min(power, sanitizeNonNegative(maxPower));
	}

	public static boolean shouldBreakBlocks(ArrowType arrowType, boolean explosiveArrowEnabled, int explosiveLevel, boolean arrowBreakBlocks, boolean enchantBreakBlocks) {
		boolean arrowSource = arrowType == ArrowType.EXPLOSIVE && explosiveArrowEnabled && arrowBreakBlocks;
		boolean enchantSource = explosiveLevel > 0 && enchantBreakBlocks;
		return arrowSource || enchantSource;
	}

	public static boolean shouldCreateFire(ArrowType arrowType, boolean explosiveArrowEnabled, boolean arrowFire) {
		return arrowType == ArrowType.EXPLOSIVE && explosiveArrowEnabled && arrowFire;
	}

	public static float scaleDamage(boolean shooter, float damage, double multiplier) {
		return shooter ? (float) (damage * sanitizeMultiplier(multiplier)) : damage;
	}

	public static Vec3d scaleKnockback(Vec3d knockback, double multiplier) {
		return knockback.multiply(sanitizeMultiplier(multiplier));
	}

	private static double sanitizeNonNegative(double value) {
		return Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D;
	}

	private static double sanitizeMultiplier(double value) {
		return Double.isFinite(value) ? Math.max(0.0D, Math.min(10.0D, value)) : 1.0D;
	}
}
