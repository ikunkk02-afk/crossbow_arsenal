package com.ikunkk02.crossbowarsenal.client.hud;

public final class StartupHudAnimation {
	private StartupHudAnimation() {
	}

	public static Phase phase(int ticks, int durationTicks) {
		int duration = Math.max(1, durationTicks);
		int bootingEnd = scaledBoundary(duration, 8);
		int onlineEnd = Math.max(bootingEnd + 1, scaledBoundary(duration, 18));
		if (ticks < bootingEnd) {
			return Phase.BOOTING;
		}
		if (ticks < onlineEnd) {
			return Phase.ONLINE;
		}
		return Phase.READY;
	}

	public static float totalProgress(float ticks, int durationTicks) {
		return clamp01(ticks / Math.max(1.0F, durationTicks));
	}

	public static float phaseProgress(float ticks, int durationTicks, Phase phase) {
		int duration = Math.max(1, durationTicks);
		float bootingEnd = scaledBoundary(duration, 8);
		float onlineEnd = Math.max(bootingEnd + 1.0F, scaledBoundary(duration, 18));
		return switch (phase) {
			case BOOTING -> clamp01(ticks / Math.max(1.0F, bootingEnd));
			case ONLINE -> clamp01((ticks - bootingEnd) / Math.max(1.0F, onlineEnd - bootingEnd));
			case READY -> clamp01((ticks - onlineEnd) / Math.max(1.0F, duration - onlineEnd));
		};
	}

	private static int scaledBoundary(int durationTicks, int defaultTick) {
		return Math.max(1, (durationTicks * defaultTick + 29) / 30);
	}

	private static float clamp01(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}

	public enum Phase {
		BOOTING,
		ONLINE,
		READY
	}
}
