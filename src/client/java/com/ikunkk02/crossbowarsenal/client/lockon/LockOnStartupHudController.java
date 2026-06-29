package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.client.hud.StartupHudState;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class LockOnStartupHudController {
	private static final StartupHudState STATE = new StartupHudState();
	private static ClientPlayerEntity lastPlayer;
	private static ClientWorld lastWorld;

	private LockOnStartupHudController() {
	}

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(LockOnStartupHudController::tick);
	}

	public static boolean isActive() {
		return STATE.isActive();
	}

	public static int getTicks() {
		return STATE.getTicks();
	}

	private static void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			resetSession();
			return;
		}
		if (client.player != lastPlayer || client.world != lastWorld) {
			STATE.resetSession();
			lastPlayer = client.player;
			lastWorld = client.world;
		}
		if (!client.player.isAlive()) {
			STATE.resetSession();
			return;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		String identity = findHeldLockOnCrossbowIdentity(client.player);
		boolean started = STATE.update(
				identity != null,
				identity,
				config.enableLockOnSight && config.enableStartupHud,
				config.startupHudReplayOnSwitch,
				config.startupHudDurationTicks
		);
		if (started && config.enableStartupSound) {
			client.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.18F, 1.35F);
		}
	}

	private static String findHeldLockOnCrossbowIdentity(ClientPlayerEntity player) {
		if (LockOnSightItemData.hasLockOnSight(player.getMainHandStack())) {
			return "main:" + player.getInventory().selectedSlot;
		}
		if (LockOnSightItemData.hasLockOnSight(player.getOffHandStack())) {
			return "offhand";
		}
		return null;
	}

	private static void resetSession() {
		STATE.resetSession();
		lastPlayer = null;
		lastWorld = null;
	}
}
