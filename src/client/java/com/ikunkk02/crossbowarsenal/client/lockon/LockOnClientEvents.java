package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.network.LockTargetPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public final class LockOnClientEvents {
	private static int lastSentTargetId = Integer.MIN_VALUE;
	private static ClientPlayerEntity lastPlayer;
	private static LivingEntity currentTarget;
	private static boolean holdingSightCrossbow;
	private static boolean hasSentLockTargetPacket;
	private static double currentTargetScreenX = Double.NaN;
	private static double currentTargetScreenY = Double.NaN;

	private LockOnClientEvents() {
	}

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(LockOnClientEvents::tick);
	}

	public static LivingEntity getCurrentTarget() {
		return currentTarget;
	}

	public static int getLastSentTargetId() {
		return lastSentTargetId;
	}

	public static boolean isHoldingSightCrossbow() {
		return holdingSightCrossbow;
	}

	public static boolean hasSentLockTargetPacket() {
		return hasSentLockTargetPacket;
	}

	public static double getCurrentTargetScreenX() {
		return currentTargetScreenX;
	}

	public static double getCurrentTargetScreenY() {
		return currentTargetScreenY;
	}

	public static boolean isCurrentTargetSynced() {
		return hasSentLockTargetPacket && ((currentTarget == null && lastSentTargetId == -1)
				|| (currentTarget != null && currentTarget.getId() == lastSentTargetId));
	}

	private static void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			currentTarget = null;
			holdingSightCrossbow = false;
			currentTargetScreenX = Double.NaN;
			currentTargetScreenY = Double.NaN;
			lastSentTargetId = Integer.MIN_VALUE;
			hasSentLockTargetPacket = false;
			lastPlayer = null;
			return;
		}
		if (client.player != lastPlayer) {
			lastPlayer = client.player;
			lastSentTargetId = Integer.MIN_VALUE;
			hasSentLockTargetPacket = false;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		holdingSightCrossbow = LockOnTargetFinder.isHoldingSightCrossbowForLock(client);
		currentTarget = config.enableLockOnSight ? LockOnTargetFinder.findTarget(client) : null;
		updateTargetScreenPosition(client);
		sendIfChanged(currentTarget == null ? -1 : currentTarget.getId());
	}

	private static void updateTargetScreenPosition(MinecraftClient client) {
		if (currentTarget == null) {
			currentTargetScreenX = Double.NaN;
			currentTargetScreenY = Double.NaN;
			return;
		}

		LockOnScreenProjection.ScreenRect rect = LockOnScreenProjection.projectEntity(client, currentTarget, 1.0F);
		if (!LockOnScreenProjection.intersectsScreen(rect, client, 0)) {
			currentTargetScreenX = Double.NaN;
			currentTargetScreenY = Double.NaN;
			return;
		}
		currentTargetScreenX = rect.centerX();
		currentTargetScreenY = rect.centerY();
	}

	private static void sendIfChanged(int targetId) {
		if (targetId != lastSentTargetId) {
			hasSentLockTargetPacket = false;
			if (!ClientPlayNetworking.canSend(LockTargetPacket.ID)) {
				return;
			}
			ClientPlayNetworking.send(new LockTargetPacket(targetId));
			lastSentTargetId = targetId;
			hasSentLockTargetPacket = true;
		}
	}
}
