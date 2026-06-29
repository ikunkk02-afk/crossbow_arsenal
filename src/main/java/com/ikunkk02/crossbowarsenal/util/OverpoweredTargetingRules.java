package com.ikunkk02.crossbowarsenal.util;

import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;
import org.jetbrains.annotations.Nullable;

public final class OverpoweredTargetingRules {
	private OverpoweredTargetingRules() {
	}

	public static @Nullable String getRestrictionReason(
			OverpoweredTargetingPolicy policy,
			boolean player,
			boolean invisible,
			boolean throughWall
	) {
		if (player && !policy.allowTargetPlayers()) {
			return "player_targeting_disabled";
		}
		if (invisible && !policy.allowTargetInvisibleEntities()) {
			return "invisible_targeting_disabled";
		}
		if (throughWall && !policy.allowTargetThroughWalls()) {
			return "through_wall_targeting_disabled";
		}
		return null;
	}

	public static int getPriority(boolean player, boolean boss, boolean undead, boolean hostile) {
		if (player) {
			return 0;
		}
		if (boss) {
			return 1;
		}
		if (undead) {
			return 2;
		}
		return hostile ? 3 : 4;
	}
}
