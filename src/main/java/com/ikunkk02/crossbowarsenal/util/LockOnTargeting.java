package com.ikunkk02.crossbowarsenal.util;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class LockOnTargeting {
	private LockOnTargeting() {
	}

	public static boolean hasUsableSightCrossbow(PlayerEntity player) {
		return getSightCrossbow(player) != ItemStack.EMPTY;
	}

	public static ItemStack getSightCrossbow(PlayerEntity player) {
		ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
		if (LockOnSightItemData.hasLockOnSight(mainHand)) {
			return mainHand;
		}

		ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);
		return LockOnSightItemData.hasLockOnSight(offHand) ? offHand : ItemStack.EMPTY;
	}

	public static boolean isValidTarget(PlayerEntity player, Entity entity) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		return isValidServerTarget(player, entity, config);
	}

	public static boolean isValidTarget(PlayerEntity player, Entity entity, double maxDistance, double angleDegrees) {
		if (!isValidBaseTarget(player, entity, maxDistance, false)) {
			return false;
		}
		return isWithinForwardAngle(player, (LivingEntity) entity, angleDegrees);
	}

	public static boolean isValidBaseTarget(PlayerEntity player, Entity entity, double maxDistance, boolean requireLineOfSight) {
		return getBaseTargetInvalidReason(player, entity, maxDistance, requireLineOfSight) == null;
	}

	public static boolean isValidServerTarget(PlayerEntity player, Entity entity, CrossbowArsenalConfig config) {
		return getServerTargetInvalidReason(player, entity, config) == null;
	}

	public static String getServerTargetInvalidReason(PlayerEntity player, Entity entity, CrossbowArsenalConfig config) {
		String baseReason = getBaseTargetInvalidReason(player, entity, config.lockOnMaxDistance, config.requireLineOfSight);
		if (baseReason != null) {
			return baseReason;
		}
		LivingEntity target = (LivingEntity) entity;
		if (config.enableFullScreenLockOn) {
			return isWithinForwardFov(player, target, config.serverValidationFovDegrees) ? null : "outside_server_validation_fov_" + config.serverValidationFovDegrees;
		}
		return isWithinForwardAngle(player, target, config.lockOnAngleDegrees) ? null : "outside_lock_angle_" + config.lockOnAngleDegrees;
	}

	private static String getBaseTargetInvalidReason(PlayerEntity player, Entity entity, double maxDistance, boolean requireLineOfSight) {
		if (!(entity instanceof LivingEntity target)) {
			return "not_living_entity";
		}
		if (entity == player) {
			return "self_target";
		}
		if (!entity.isAlive() || target.isDead()) {
			return "dead_target";
		}
		if (!entity.canHit() || !entity.canBeHitByProjectile()) {
			return "target_cannot_be_hit";
		}
		if (target.isSpectator()) {
			return "spectator_target";
		}
		if (target instanceof PlayerEntity targetPlayer && (targetPlayer.isCreative() || targetPlayer.isSpectator())) {
			return "creative_or_spectator_player";
		}

		Vec3d eye = player.getEyePos();
		Vec3d targetPos = getTargetPoint(target);
		if (eye.squaredDistanceTo(targetPos) > maxDistance * maxDistance) {
			return "too_far";
		}
		if (requireLineOfSight && !player.canSee(target)) {
			return "no_line_of_sight";
		}
		return null;
	}

	public static boolean isWithinForwardFov(PlayerEntity player, LivingEntity target, double fullFovDegrees) {
		Vec3d eye = player.getEyePos();
		Vec3d targetPos = getTargetPoint(target);
		Vec3d toTarget = targetPos.subtract(eye);
		if (toTarget.lengthSquared() <= 1.0E-6D) {
			return false;
		}
		double dot = player.getRotationVec(1.0F).normalize().dotProduct(toTarget.normalize());
		return LockOnMath.isWithinForwardCone(dot, fullFovDegrees);
	}

	public static boolean isWithinForwardAngle(PlayerEntity player, LivingEntity target, double angleDegrees) {
		Vec3d eye = player.getEyePos();
		Vec3d targetPos = getTargetPoint(target);
		Vec3d toTarget = targetPos.subtract(eye).normalize();
		double dot = player.getRotationVec(1.0F).normalize().dotProduct(toTarget);
		return dot >= Math.cos(Math.toRadians(angleDegrees));
	}

	public static Vec3d getTargetPoint(LivingEntity target) {
		return target.getPos().add(0.0D, target.getHeight() * 0.55D, 0.0D);
	}

	public static Vec3d getHomingTargetPoint(LivingEntity target) {
		return target.getEyePos();
	}

	public static int getPriority(LivingEntity target) {
		if (isBoss(target)) {
			return 0;
		}
		if (isUndead(target)) {
			return 1;
		}
		if (target instanceof HostileEntity) {
			return 2;
		}
		return 3;
	}

	public static boolean isBoss(LivingEntity target) {
		return target instanceof EnderDragonEntity || target instanceof WitherEntity;
	}

	public static boolean isUndead(LivingEntity target) {
		return target.getType().isIn(EntityTypeTags.UNDEAD) || target instanceof WitherEntity;
	}

	public static double getHomingStrength(LivingEntity target) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (isBoss(target)) {
			return config.bossHomingStrength;
		}
		if (isUndead(target)) {
			return config.undeadHomingStrength;
		}
		return config.normalHomingStrength;
	}
}
