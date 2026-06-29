package com.ikunkk02.crossbowarsenal.arrow;

public final class SpecialArrowRules {
	private static final int MAX_PENETRATED_BLOCKS = 64;

	private SpecialArrowRules() {
	}

	public static int getPenetratingArrowMaxBlocks(int piercingLevel, int configuredBase) {
		int base = Math.max(0, configuredBase);
		int bonus = Math.max(0, piercingLevel - 3);
		return Math.min(MAX_PENETRATED_BLOCKS, base + bonus);
	}

	public static boolean canPenetrateFragile(ArrowType arrowType, boolean enabled, boolean fragileEnabled) {
		return arrowType == ArrowType.PENETRATING && enabled && fragileEnabled;
	}

	public static boolean canPenetrateSoft(ArrowType arrowType, boolean enabled, int piercingLevel, int requiredLevel) {
		return arrowType == ArrowType.PENETRATING && enabled && piercingLevel >= Math.max(0, requiredLevel);
	}

	public static boolean canPenetrateWooden(ArrowType arrowType, boolean enabled, int piercingLevel, int requiredLevel) {
		return arrowType == ArrowType.PENETRATING && enabled && piercingLevel >= Math.max(0, requiredLevel);
	}

	public static boolean shouldExplodeBeforePenetration(ArrowType arrowType, boolean canExplode, boolean explosiveStopsPenetration) {
		return canExplode && (arrowType != ArrowType.PENETRATING || explosiveStopsPenetration);
	}

	public static boolean shouldExplodeAfterSuccessfulPenetration(ArrowType arrowType, boolean canExplode, boolean explosiveStopsPenetration) {
		return arrowType == ArrowType.PENETRATING && canExplode && !explosiveStopsPenetration;
	}

	public static boolean isExplosiveArrowActive(ArrowType arrowType, boolean explosiveArrowEnabled) {
		return arrowType == ArrowType.EXPLOSIVE && explosiveArrowEnabled;
	}
}
