package com.ikunkk02.crossbowarsenal.client.hud;

import com.ikunkk02.crossbowarsenal.client.lockon.ClientTargetingPolicyState;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnTargetFinder;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public final class OverpoweredWarningController {
	private static final OverpoweredWarningState STATE = new OverpoweredWarningState();
	private static ClientWorld lastWorld;

	private OverpoweredWarningController() {
	}

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(OverpoweredWarningController::tick);
	}

	public static OverpoweredWarningState.Mode getMode() {
		return STATE.getMode();
	}

	public static int getTicksRemaining() {
		return STATE.getTicksRemaining();
	}

	private static void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			resetSession();
			return;
		}
		if (client.world != lastWorld) {
			STATE.resetSession();
			lastWorld = client.world;
		}
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		STATE.update(
				config.showOverpoweredWarning,
				ClientTargetingPolicyState.getEffectivePolicy().enabled(),
				LockOnTargetFinder.isHoldingSightCrossbowForLock(client)
		);
	}

	private static void resetSession() {
		STATE.resetSession();
		lastWorld = null;
	}
}
