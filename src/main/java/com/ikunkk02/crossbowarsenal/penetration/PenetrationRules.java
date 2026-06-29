package com.ikunkk02.crossbowarsenal.penetration;

import net.minecraft.util.math.Vec3d;

public final class PenetrationRules {
	public static final double MIN_PENETRATION_SPEED = 0.2D;

	private PenetrationRules() {
	}

	public static int getMaxEntityTargets(int pierceLevel, int configuredMax) {
		if (pierceLevel <= 0 || configuredMax <= 0) {
			return 0;
		}
		return Math.min(configuredMax, pierceLevel + 1);
	}

	public static int getMaxFragileBlocks(int pierceLevel, boolean homing, int configuredMax) {
		if (configuredMax <= 0) {
			return 0;
		}
		return homing ? configuredMax : Math.min(configuredMax, Math.max(0, pierceLevel));
	}

	public static boolean hasSufficientSpeed(double speed) {
		return Double.isFinite(speed) && speed >= MIN_PENETRATION_SPEED;
	}

	public static boolean canPenetrateGlass(boolean glassEnabled, boolean homing, boolean lockOnCanPenetrateGlass) {
		return glassEnabled && (!homing || lockOnCanPenetrateGlass);
	}

	public static boolean canPenetrateFragile(boolean fragileEnabled, int pierceLevel, boolean homing, boolean lockOnCanPenetrateFragile) {
		return fragileEnabled && (pierceLevel > 0 || homing && lockOnCanPenetrateFragile);
	}

	public static Vec3d reduceVelocity(Vec3d velocity, double multiplier) {
		double safeMultiplier = Double.isFinite(multiplier) ? Math.max(0.0D, Math.min(1.0D, multiplier)) : 1.0D;
		return velocity.multiply(safeMultiplier);
	}

	public static Vec3d getExitPosition(Vec3d hitPosition, Vec3d velocity) {
		if (velocity.lengthSquared() <= 1.0E-12D) {
			return hitPosition;
		}
		return hitPosition.add(velocity.normalize().multiply(0.05D));
	}

	public static boolean isBlockBeforeEntity(Vec3d start, Vec3d blockHitPosition, Vec3d entityHitPosition) {
		return entityHitPosition == null || start.squaredDistanceTo(blockHitPosition) < start.squaredDistanceTo(entityHitPosition);
	}

	public static boolean canTriggerGuaranteedHit(boolean enabled, boolean expandedHitboxIntersected, boolean clearPath) {
		return enabled && expandedHitboxIntersected && clearPath;
	}

	public static boolean canUseOverpoweredPenetration(
			boolean overpoweredEnabled,
			boolean specialPenetratingArrow,
			boolean throughWallHomingActive
	) {
		return overpoweredEnabled && (specialPenetratingArrow || throughWallHomingActive);
	}

	public static boolean canBreakOverpoweredBlock(
			boolean overpoweredActive,
			boolean allowedBlock,
			boolean neverBreakBlock,
			boolean hasBlockEntity,
			boolean categoryEnabled,
			int penetratedCount,
			int configuredMax
	) {
		return overpoweredActive
				&& allowedBlock
				&& !neverBreakBlock
				&& !hasBlockEntity
				&& categoryEnabled
				&& configuredMax > 0
				&& penetratedCount >= 0
				&& penetratedCount < configuredMax;
	}
}
