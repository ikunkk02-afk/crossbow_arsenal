package com.ikunkk02.crossbowarsenal.lockon;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.util.LockOnMath;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LockOnManager {
	private static final Map<UUID, UUID> PLAYER_TARGETS = new HashMap<>();

	private LockOnManager() {
	}

	public static void initialize() {
	}

	public static void handleTargetPacket(ServerPlayerEntity player, int targetEntityId) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		debug(config, "Received LockTargetPacket from {} targetId={}", player.getName().getString(), targetEntityId);
		if (targetEntityId < 0) {
			clearTarget(player, config, "client_lost_target");
			return;
		}

		if (!config.enableLockOnSight) {
			clearTarget(player, config, "lock_on_disabled");
			return;
		}
		if (!LockOnTargeting.hasUsableSightCrossbow(player)) {
			clearTarget(player, config, "player_not_holding_lock_on_crossbow");
			return;
		}
		Entity entity = player.getWorld().getEntityById(targetEntityId);
		String invalidReason = LockOnTargeting.getServerTargetInvalidReason(player, entity, config);
		if (invalidReason != null) {
			clearTarget(player, config, invalidReason);
			return;
		}

		PLAYER_TARGETS.put(player.getUuid(), entity.getUuid());
		debug(config, "Saved lock target for {}: {} ({})", player.getName().getString(), entity.getName().getString(), entity.getUuid());
	}

	public static void attachHomingIfPresent(LivingEntity shooter, PersistentProjectileEntity projectile, boolean repeatingShot) {
		if (!(shooter instanceof ServerPlayerEntity player)) {
			return;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (!(projectile.getWorld() instanceof ServerWorld serverWorld)) {
			debug(config, "Skipped homing for {}: projectile is not in a server world", player.getName().getString());
			return;
		}

		UUID targetUuid = PLAYER_TARGETS.get(player.getUuid());
		if (targetUuid == null) {
			debug(config, "Skipped homing for {}: no saved lock target", player.getName().getString());
			return;
		}
		if (!config.enableLockOnSight) {
			clearTarget(player, config, "lock_on_disabled_at_shot");
			return;
		}
		if (!LockOnTargeting.hasUsableSightCrossbow(player)) {
			clearTarget(player, config, "player_not_holding_lock_on_crossbow_at_shot");
			return;
		}

		Entity entity = serverWorld.getEntity(targetUuid);
		String invalidReason = LockOnTargeting.getServerTargetInvalidReason(player, entity, config);
		if (invalidReason != null) {
			clearTarget(player, config, "shot_validation_failed_" + invalidReason);
			return;
		}

		LivingEntity target = (LivingEntity) entity;
		double strength = LockOnTargeting.getHomingStrength(target);
		if (repeatingShot) {
			strength *= config.repeatingHomingMultiplier;
		}
		if (projectile instanceof HomingProjectile homingProjectile) {
			homingProjectile.crossbow_arsenal$setHomingTarget(target.getUuid(), config.lockOnHomingTicks, LockOnMath.clampHomingStrength(strength), config.lockOnMaxDistance);
			debug(config, "Attached homing target for {} projectile={} target={} repeating={} strength={} ticks={}", player.getName().getString(), projectile.getUuid(), target.getUuid(), repeatingShot, LockOnMath.clampHomingStrength(strength), config.lockOnHomingTicks);
		} else {
			debug(config, "Skipped homing for {}: projectile does not implement HomingProjectile ({})", player.getName().getString(), projectile.getClass().getName());
		}
	}

	private static void clearTarget(ServerPlayerEntity player, CrossbowArsenalConfig config, String reason) {
		UUID previous = PLAYER_TARGETS.remove(player.getUuid());
		debug(config, "Cleared lock target for {}: {} previous={}", player.getName().getString(), reason, previous);
	}

	private static void debug(CrossbowArsenalConfig config, String message, Object... args) {
		if (config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info("[Lock-on] " + message, args);
		}
	}
}
