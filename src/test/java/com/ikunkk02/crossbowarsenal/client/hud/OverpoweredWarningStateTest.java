package com.ikunkk02.crossbowarsenal.client.hud;

public final class OverpoweredWarningStateTest {
	private OverpoweredWarningStateTest() {
	}

	public static void main(String[] args) {
		OverpoweredWarningState state = new OverpoweredWarningState();
		assertEquals(OverpoweredWarningState.Mode.NONE, state.update(true, false, false), "disabled policy shows nothing");
		assertEquals(OverpoweredWarningState.Mode.FULL, state.update(true, true, false), "enabling in a world starts full warning");
		assertEquals(60, state.getTicksRemaining(), "full warning lasts 60 ticks");
		state.update(true, true, false);
		assertEquals(59, state.getTicksRemaining(), "warning advances instead of restarting every tick");

		assertEquals(OverpoweredWarningState.Mode.SHORT, state.update(true, true, true), "first switch to lock-on crossbow starts short warning");
		assertEquals(30, state.getTicksRemaining(), "short warning lasts 30 ticks");
		state.update(true, true, false);
		assertEquals(OverpoweredWarningState.Mode.SHORT, state.update(true, true, true), "later switches do not start another warning");
		assertTrue(state.getTicksRemaining() < 30, "later switch continues existing countdown");

		state.resetSession();
		assertEquals(OverpoweredWarningState.Mode.FULL, state.update(true, true, true), "new world starts full warning");
		assertTrue(state.hasShownSwitchWarning(), "holding on world entry consumes first switch warning");
		for (int tick = 0; tick < 60; tick++) {
			state.update(true, true, true);
		}
		assertEquals(OverpoweredWarningState.Mode.NONE, state.getMode(), "full warning expires");

		state.resetSession();
		assertEquals(OverpoweredWarningState.Mode.NONE, state.update(false, true, false), "local warning preference suppresses display");
		assertEquals(OverpoweredWarningState.Mode.FULL, state.update(true, true, false), "enabling warning preference later still shows unused full warning");
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!java.util.Objects.equals(expected, actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
