package com.ikunkk02.crossbowarsenal.util;

public final class LockOnMath {
	private LockOnMath() {
	}

	public static boolean isWithinForwardCone(double normalizedDot, double fullFovDegrees) {
		double fov = Math.max(1.0D, Math.min(180.0D, fullFovDegrees));
		return normalizedDot + 1.0E-9D >= Math.cos(Math.toRadians(fov * 0.5D));
	}

	public static double clampHomingStrength(double strength) {
		return Math.max(0.0D, Math.min(1.0D, strength));
	}

	public static double screenXFromNdc(double ndcX, int screenWidth) {
		return (ndcX + 1.0D) * 0.5D * screenWidth;
	}

	public static double screenYFromNdc(double ndcY, int screenHeight) {
		return (1.0D - ndcY) * 0.5D * screenHeight;
	}
}
