package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;

public final class ClientTargetingPolicyStateTest {
	private ClientTargetingPolicyStateTest() {
	}

	public static void main(String[] args) {
		CrossbowArsenalConfig local = new CrossbowArsenalConfig();
		local.enableOverpoweredTargeting = true;
		local.allowTargetPlayers = true;
		local.allowTargetThroughWalls = true;
		local.overpoweredTargetMaxDistance = 96.0D;

		ClientTargetingPolicyState.reset();
		assertFalse(ClientTargetingPolicyState.getEffectivePolicy(local).enabled(), "missing server policy defaults closed");

		ClientTargetingPolicyState.acceptServerPolicy(new OverpoweredTargetingPolicy(true, true, false, true, true, 64.0D));
		OverpoweredTargetingPolicy effective = ClientTargetingPolicyState.getEffectivePolicy(local);
		assertTrue(effective.enabled(), "server and client master switches enable policy");
		assertTrue(effective.allowTargetPlayers(), "matching player flags remain enabled");
		assertFalse(effective.allowTargetThroughWalls(), "server wall restriction wins");
		assertDoubleEquals(64.0D, effective.overpoweredTargetMaxDistance(), "server distance cap wins");

		ClientTargetingPolicyState.acceptLockResult(42, true, true, "none");
		assertEquals(42, ClientTargetingPolicyState.getLastResultTargetId(), "lock result target id stored");
		assertTrue(ClientTargetingPolicyState.wasLastTargetAccepted(), "accepted result stored");
		assertTrue(ClientTargetingPolicyState.wasLastAcceptedTargetThroughWall(), "through-wall acceptance stored");
		assertEquals("none", ClientTargetingPolicyState.getLastRejectedReason(), "accepted result clears rejection");

		ClientTargetingPolicyState.acceptLockResult(43, false, false, "player_targeting_disabled");
		assertFalse(ClientTargetingPolicyState.wasLastTargetAccepted(), "rejection result stored");
		assertEquals("player_targeting_disabled", ClientTargetingPolicyState.getLastRejectedReason(), "rejection reason stored");

		ClientTargetingPolicyState.reset();
		assertEquals(Integer.MIN_VALUE, ClientTargetingPolicyState.getLastResultTargetId(), "disconnect reset clears result");
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

	private static void assertDoubleEquals(double expected, double actual, String message) {
		if (Double.compare(expected, actual) != 0) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
