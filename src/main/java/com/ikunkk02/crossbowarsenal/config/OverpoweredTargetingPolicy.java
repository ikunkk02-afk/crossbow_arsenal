package com.ikunkk02.crossbowarsenal.config;

public record OverpoweredTargetingPolicy(
		boolean enabled,
		boolean allowTargetPlayers,
		boolean allowTargetThroughWalls,
		boolean allowHomingThroughWalls,
		boolean allowTargetInvisibleEntities,
		double overpoweredTargetMaxDistance
) {
	private static final double DEFAULT_MAX_DISTANCE = 64.0D;

	public OverpoweredTargetingPolicy {
		overpoweredTargetMaxDistance = sanitizeDistance(overpoweredTargetMaxDistance);
		if (!enabled) {
			allowTargetPlayers = false;
			allowTargetThroughWalls = false;
			allowHomingThroughWalls = false;
			allowTargetInvisibleEntities = false;
		}
	}

	public static OverpoweredTargetingPolicy disabled() {
		return new OverpoweredTargetingPolicy(false, false, false, false, false, DEFAULT_MAX_DISTANCE);
	}

	public static OverpoweredTargetingPolicy fromConfig(CrossbowArsenalConfig config) {
		return new OverpoweredTargetingPolicy(
				config.enableOverpoweredTargeting,
				config.allowTargetPlayers,
				config.allowTargetThroughWalls,
				config.allowHomingThroughWalls,
				config.allowTargetInvisibleEntities,
				config.overpoweredTargetMaxDistance
		);
	}

	public OverpoweredTargetingPolicy intersect(OverpoweredTargetingPolicy serverPolicy) {
		boolean effectiveEnabled = enabled && serverPolicy.enabled;
		return new OverpoweredTargetingPolicy(
				effectiveEnabled,
				allowTargetPlayers && serverPolicy.allowTargetPlayers,
				allowTargetThroughWalls && serverPolicy.allowTargetThroughWalls,
				allowHomingThroughWalls && serverPolicy.allowHomingThroughWalls,
				allowTargetInvisibleEntities && serverPolicy.allowTargetInvisibleEntities,
				Math.min(overpoweredTargetMaxDistance, serverPolicy.overpoweredTargetMaxDistance)
		);
	}

	public double targetMaxDistance(double normalMaxDistance) {
		return enabled ? overpoweredTargetMaxDistance : normalMaxDistance;
	}

	private static double sanitizeDistance(double distance) {
		if (!Double.isFinite(distance)) {
			return DEFAULT_MAX_DISTANCE;
		}
		return Math.max(1.0D, Math.min(128.0D, distance));
	}
}
