package com.ikunkk02.crossbowarsenal.client.hud;

import java.util.Objects;

public final class StartupHudState {
	private boolean active;
	private boolean hasPlayedThisSession;
	private int ticks;
	private String lastCrossbowIdentity;

	/**
	 * Advances the startup animation and returns true only on the tick where a new animation starts.
	 */
	public boolean update(boolean holdingLockOnCrossbow, String crossbowIdentity, boolean enabled, boolean replayOnSwitch, int durationTicks) {
		if (!enabled || !holdingLockOnCrossbow || crossbowIdentity == null) {
			active = false;
			ticks = 0;
			lastCrossbowIdentity = null;
			return false;
		}

		boolean switchedCrossbow = !Objects.equals(lastCrossbowIdentity, crossbowIdentity);
		lastCrossbowIdentity = crossbowIdentity;
		if (switchedCrossbow && (replayOnSwitch || !hasPlayedThisSession)) {
			active = true;
			hasPlayedThisSession = true;
			ticks = 0;
			return true;
		}

		if (active) {
			ticks++;
			if (ticks >= Math.max(1, durationTicks)) {
				active = false;
			}
		}
		return false;
	}

	public void resetSession() {
		active = false;
		hasPlayedThisSession = false;
		ticks = 0;
		lastCrossbowIdentity = null;
	}

	public boolean isActive() {
		return active;
	}

	public int getTicks() {
		return ticks;
	}
}
