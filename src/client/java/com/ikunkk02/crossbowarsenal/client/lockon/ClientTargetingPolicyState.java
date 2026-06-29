package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;

public final class ClientTargetingPolicyState {
	private static OverpoweredTargetingPolicy serverPolicy;
	private static int lastResultTargetId = Integer.MIN_VALUE;
	private static boolean lastTargetAccepted;
	private static boolean lastAcceptedTargetThroughWall;
	private static String lastRejectedReason = "none";

	private ClientTargetingPolicyState() {
	}

	public static void acceptServerPolicy(OverpoweredTargetingPolicy policy) {
		serverPolicy = policy;
	}

	public static OverpoweredTargetingPolicy getEffectivePolicy() {
		return getEffectivePolicy(CrossbowArsenalConfigManager.getConfig());
	}

	static OverpoweredTargetingPolicy getEffectivePolicy(CrossbowArsenalConfig localConfig) {
		if (serverPolicy == null) {
			return OverpoweredTargetingPolicy.disabled();
		}
		return OverpoweredTargetingPolicy.fromConfig(localConfig).intersect(serverPolicy);
	}

	public static void acceptLockResult(int targetId, boolean accepted, boolean throughWall, String reason) {
		lastResultTargetId = targetId;
		lastTargetAccepted = accepted;
		lastAcceptedTargetThroughWall = accepted && throughWall;
		lastRejectedReason = accepted ? "none" : reason;
	}

	public static int getLastResultTargetId() {
		return lastResultTargetId;
	}

	public static boolean wasLastTargetAccepted() {
		return lastTargetAccepted;
	}

	public static boolean wasLastAcceptedTargetThroughWall() {
		return lastAcceptedTargetThroughWall;
	}

	public static String getLastRejectedReason() {
		return lastRejectedReason;
	}

	public static void reset() {
		serverPolicy = null;
		lastResultTargetId = Integer.MIN_VALUE;
		lastTargetAccepted = false;
		lastAcceptedTargetThroughWall = false;
		lastRejectedReason = "none";
	}
}
