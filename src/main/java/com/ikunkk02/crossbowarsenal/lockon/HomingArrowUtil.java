package com.ikunkk02.crossbowarsenal.lockon;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.component.LockTargetComponent;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;
import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingCrossbowManager;
import com.ikunkk02.crossbowarsenal.util.LockOnMath;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class HomingArrowUtil {
	private HomingArrowUtil() {
	}

	public static void applyHomingIfPossible(ServerPlayerEntity player, ItemStack crossbowStack, PersistentProjectileEntity projectile) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		LockTargetComponent component = LockOnManager.getTargetComponent(player);
		UUID targetUuid = component.getLockedTargetUuid();
		boolean sightInstalled = LockOnSightItemData.hasLockOnSight(crossbowStack);
		Entity targetEntity = projectile.getWorld() instanceof ServerWorld serverWorld && targetUuid != null
				? serverWorld.getEntity(targetUuid)
				: null;
		String targetName = targetEntity == null ? "<missing>" : targetEntity.getName().getString();

		if (!config.enableLockOnSight) {
			logShot(config, player, projectile, sightInstalled, component, targetName, targetEntity != null, false, "lock_on_disabled", 0.0D, 0.0D);
			LockOnManager.clearTarget(player, "lock_on_disabled_at_shot");
			return;
		}
		if (!sightInstalled) {
			logShot(config, player, projectile, false, component, targetName, targetEntity != null, false, "fired_crossbow_has_no_lock_on_sight", 0.0D, 0.0D);
			return;
		}
		if (targetUuid == null) {
			logShot(config, player, projectile, true, component, targetName, false, false, "no_saved_lock_target", 0.0D, 0.0D);
			return;
		}
		if (!(projectile.getWorld() instanceof ServerWorld)) {
			logShot(config, player, projectile, true, component, targetName, targetEntity != null, false, "projectile_not_in_server_world", 0.0D, 0.0D);
			return;
		}

		String invalidReason = LockOnTargeting.getServerTargetInvalidReason(player, targetEntity, config);
		if (invalidReason != null) {
			logShot(config, player, projectile, true, component, targetName, targetEntity != null, false, "shot_validation_failed_" + invalidReason, 0.0D, 0.0D);
			LockOnManager.clearTarget(player, "shot_validation_failed_" + invalidReason);
			return;
		}
		if (!(projectile instanceof HomingProjectile homingProjectile)) {
			logShot(config, player, projectile, true, component, targetName, true, false, "projectile_missing_homing_interface", 0.0D, 0.0D);
			return;
		}

		double originalSpeed = projectile.getVelocity().length();
		if (originalSpeed <= 1.0E-6D) {
			logShot(config, player, projectile, true, component, targetName, true, false, "invalid_original_speed", 0.0D, originalSpeed);
			return;
		}

		LivingEntity target = (LivingEntity) targetEntity;
		OverpoweredTargetingPolicy policy = OverpoweredTargetingPolicy.fromConfig(config);
		boolean repeatingShot = RepeatingCrossbowManager.isFiring(player);
		double strength = LockOnTargeting.getHomingStrength(target);
		if (repeatingShot) {
			strength *= config.repeatingHomingMultiplier;
		}
		strength = LockOnMath.clampHomingStrength(strength);
		boolean homingThroughWallsAuthorized = policy.allowHomingThroughWalls();
		homingProjectile.crossbow_arsenal$setHomingTarget(
				targetUuid,
				config.lockOnHomingTicks,
				strength,
				policy.targetMaxDistance(config.lockOnMaxDistance),
				originalSpeed,
				homingThroughWallsAuthorized
		);

		Vec3d aimPoint = LockOnTargeting.getHomingAimPoint(target);
		Vec3d predictedAimPoint = LockOnMath.predictAimPoint(projectile.getPos(), aimPoint, target.getVelocity(), originalSpeed, config.homingGravityCompensation);
		Vec3d desiredDirection = predictedAimPoint.subtract(projectile.getPos());
		double spreadCorrectionStrength = repeatingShot ? 0.5D : 0.65D;
		Vec3d correctedVelocity = LockOnMath.steerVelocity(projectile.getVelocity(), desiredDirection, spreadCorrectionStrength);
		projectile.setVelocity(correctedVelocity);
		updateRotation(projectile, correctedVelocity);
		projectile.velocityModified = true;
		logShot(config, player, projectile, true, component, targetName, true, true, homingThroughWallsAuthorized ? "none_through_wall_authorized" : "none", strength, originalSpeed);
	}

	private static void updateRotation(PersistentProjectileEntity projectile, Vec3d velocity) {
		projectile.setYaw((float) (MathHelper.atan2(velocity.x, velocity.z) * 57.2957763671875D));
		projectile.setPitch((float) (MathHelper.atan2(velocity.y, velocity.horizontalLength()) * 57.2957763671875D));
	}

	private static void logShot(CrossbowArsenalConfig config, ServerPlayerEntity player, PersistentProjectileEntity projectile, boolean sightInstalled, LockTargetComponent component, String targetName, boolean targetFound, boolean attached, String reason, double strength, double originalSpeed) {
		if (!config.showLockOnDebug) {
			return;
		}
		Crossbow_arsenal.LOGGER.info(
				"[Lock-on] Crossbow shot player={} sightCrossbow={} savedTargetUuid={} savedTargetEntityId={} targetName={} targetFound={} homingAttached={} reason={} arrowUuid={} arrowEntityId={} homingTicks={} homingStrength={} originalSpeed={}",
				player.getName().getString(), sightInstalled, component.getLockedTargetUuid(), component.getLockedTargetEntityId(), targetName,
				targetFound, attached, reason, projectile.getUuid(), projectile.getId(), config.lockOnHomingTicks, strength, originalSpeed
		);
	}
}
