package com.ikunkk02.crossbowarsenal.client.hud;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class StartupHudStateTest {
	private StartupHudStateTest() {
	}

	public static void main(String[] args) throws Exception {
		Class<?> stateClass = requireClass("com.ikunkk02.crossbowarsenal.client.hud.StartupHudState");
		Constructor<?> constructor = stateClass.getConstructor();
		Method update = stateClass.getMethod(
				"update", boolean.class, String.class, boolean.class, boolean.class, int.class
		);
		Method isActive = stateClass.getMethod("isActive");
		Method getTicks = stateClass.getMethod("getTicks");
		Method resetSession = stateClass.getMethod("resetSession");

		Object state = constructor.newInstance();
		assertTrue(callUpdate(update, state, true, "main:0", true, true, 30), "first lock-on crossbow starts HUD");
		assertTrue((boolean) isActive.invoke(state), "HUD is active after starting");
		assertEquals(0, getTicks.invoke(state), "start frame begins at tick zero");
		assertFalse(callUpdate(update, state, true, "main:0", true, true, 30), "same held crossbow does not restart");
		assertEquals(1, getTicks.invoke(state), "same held crossbow advances animation");

		for (int tick = 1; tick < 30; tick++) {
			callUpdate(update, state, true, "main:0", true, true, 30);
		}
		assertFalse((boolean) isActive.invoke(state), "animation stops at configured duration");
		assertFalse(callUpdate(update, state, true, "main:0", true, true, 30), "holding after completion does not replay");

		assertFalse(callUpdate(update, state, false, null, true, true, 30), "switching away does not start HUD");
		assertTrue(callUpdate(update, state, true, "main:0", true, true, 30), "switching back replays when enabled");
		assertTrue(callUpdate(update, state, true, "main:1", true, true, 30), "switching to another lock-on crossbow restarts");

		resetSession.invoke(state);
		assertTrue(callUpdate(update, state, true, "offhand", true, false, 30), "first session activation plays with replay disabled");
		callUpdate(update, state, false, null, true, false, 30);
		assertFalse(callUpdate(update, state, true, "offhand", true, false, 30), "replay disabled suppresses later switches in same session");
		resetSession.invoke(state);
		assertTrue(callUpdate(update, state, true, "offhand", true, false, 30), "session reset permits startup again");

		resetSession.invoke(state);
		assertFalse(callUpdate(update, state, true, "main:0", false, true, 30), "disabled startup HUD never starts");
		assertFalse((boolean) isActive.invoke(state), "disabled startup HUD remains inactive");

		Class<?> animationClass = requireClass("com.ikunkk02.crossbowarsenal.client.hud.StartupHudAnimation");
		Method phase = animationClass.getMethod("phase", int.class, int.class);
		assertEquals("BOOTING", phase.invoke(null, 0, 30).toString(), "phase 0-7 is booting");
		assertEquals("ONLINE", phase.invoke(null, 8, 30).toString(), "phase 8-17 is online");
		assertEquals("READY", phase.invoke(null, 18, 30).toString(), "phase 18-29 is ready");
		assertEquals("ONLINE", phase.invoke(null, 4, 15).toString(), "phase thresholds scale with duration");
	}

	private static boolean callUpdate(Method update, Object state, boolean holding, String identity, boolean enabled, boolean replay, int duration) throws Exception {
		return (boolean) update.invoke(state, holding, identity, enabled, replay, duration);
	}

	private static Class<?> requireClass(String name) {
		try {
			return Class.forName(name, false, StartupHudStateTest.class.getClassLoader());
		} catch (ClassNotFoundException exception) {
			throw new AssertionError("Missing production class " + name, exception);
		}
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertFalse(boolean condition, String message) {
		assertTrue(!condition, message);
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!java.util.Objects.equals(expected, actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
