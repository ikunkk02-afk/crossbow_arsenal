package com.ikunkk02.crossbowarsenal.lockon;

public final class OverpoweredHomingRules {
	private OverpoweredHomingRules() {
	}

	public static boolean isThroughWallHomingActive(boolean authorizedAtShot, boolean serverAllowsNow) {
		return authorizedAtShot && serverAllowsNow;
	}

	public static boolean canBypassGuaranteedHitClearPath(boolean requireClearPath, boolean throughWallHomingActive) {
		return !requireClearPath || throughWallHomingActive;
	}

}
