package com.ikunkk02.crossbowarsenal.lockon;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.component.LockTargetComponent;
import com.ikunkk02.crossbowarsenal.component.ModComponents;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;

public final class LockOnManager {
	private LockOnManager() {
	}

	public static void initialize() {
	}

	public static void handleTargetPacket(ServerPlayerEntity player, int targetEntityId) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		Entity entity = targetEntityId < 0 ? null : player.getWorld().getEntityById(targetEntityId);
		String targetName = entity == null ? "<missing>" : entity.getName().getString();
		if (targetEntityId < 0) {
			debug(config, "LockTargetPacket player={} targetEntityId={} targetName=<none> validation=true failureReason=client_lost_target", player.getName().getString(), targetEntityId);
			clearTarget(player, "client_lost_target");
			return;
		}

		if (!config.enableLockOnSight) {
			rejectTarget(player, config, targetEntityId, targetName, "lock_on_disabled");
			return;
		}
		if (!LockOnTargeting.hasUsableSightCrossbow(player)) {
			rejectTarget(player, config, targetEntityId, targetName, "player_not_holding_lock_on_crossbow");
			return;
		}
		String invalidReason = LockOnTargeting.getServerTargetInvalidReason(player, entity, config);
		if (invalidReason != null) {
			rejectTarget(player, config, targetEntityId, targetName, invalidReason);
			return;
		}

		LockTargetComponent component = getTargetComponent(player);
		component.setTarget(entity.getUuid(), targetEntityId, player.getWorld().getTime());
		debug(config, "LockTargetPacket player={} targetEntityId={} targetName={} validation=true failureReason=none targetUuid={} lastLockTime={}", player.getName().getString(), targetEntityId, targetName, entity.getUuid(), component.getLastLockTime());
	}

	public static LockTargetComponent getTargetComponent(ServerPlayerEntity player) {
		return ModComponents.LOCK_TARGET.get(player);
	}

	public static void clearTarget(ServerPlayerEntity player, String reason) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		LockTargetComponent component = getTargetComponent(player);
		UUID previousUuid = component.getLockedTargetUuid();
		int previousEntityId = component.getLockedTargetEntityId();
		component.clearTarget();
		debug(config, "Cleared player target player={} reason={} previousTargetUuid={} previousTargetEntityId={}", player.getName().getString(), reason, previousUuid, previousEntityId);
	}

	private static void rejectTarget(ServerPlayerEntity player, CrossbowArsenalConfig config, int targetEntityId, String targetName, String reason) {
		debug(config, "LockTargetPacket player={} targetEntityId={} targetName={} validation=false failureReason={}", player.getName().getString(), targetEntityId, targetName, reason);
		clearTarget(player, reason);
	}

	private static void debug(CrossbowArsenalConfig config, String message, Object... args) {
		if (config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info("[Lock-on] " + message, args);
		}
	}
}
