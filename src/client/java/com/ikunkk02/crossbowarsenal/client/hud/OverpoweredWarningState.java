package com.ikunkk02.crossbowarsenal.client.hud;

public final class OverpoweredWarningState {
	private Mode mode = Mode.NONE;
	private int ticksRemaining;
	private boolean hasShownFullWarning;
	private boolean hasShownSwitchWarning;
	private boolean wasHoldingLockOnCrossbow;

	public Mode update(boolean warningsEnabled, boolean effectiveEnabled, boolean holdingLockOnCrossbow) {
		if (!warningsEnabled || !effectiveEnabled) {
			mode = Mode.NONE;
			ticksRemaining = 0;
			wasHoldingLockOnCrossbow = holdingLockOnCrossbow;
			return mode;
		}

		if (!hasShownFullWarning) {
			hasShownFullWarning = true;
			hasShownSwitchWarning = holdingLockOnCrossbow;
			wasHoldingLockOnCrossbow = holdingLockOnCrossbow;
			mode = Mode.FULL;
			ticksRemaining = 60;
			return mode;
		}

		if (!wasHoldingLockOnCrossbow && holdingLockOnCrossbow && !hasShownSwitchWarning) {
			hasShownSwitchWarning = true;
			mode = Mode.SHORT;
			ticksRemaining = 30;
		} else if (ticksRemaining > 0) {
			ticksRemaining--;
			if (ticksRemaining == 0) {
				mode = Mode.NONE;
			}
		}
		wasHoldingLockOnCrossbow = holdingLockOnCrossbow;
		return mode;
	}

	public void resetSession() {
		mode = Mode.NONE;
		ticksRemaining = 0;
		hasShownFullWarning = false;
		hasShownSwitchWarning = false;
		wasHoldingLockOnCrossbow = false;
	}

	public Mode getMode() {
		return mode;
	}

	public int getTicksRemaining() {
		return ticksRemaining;
	}

	public boolean hasShownSwitchWarning() {
		return hasShownSwitchWarning;
	}

	public enum Mode {
		NONE,
		FULL,
		SHORT
	}
}
