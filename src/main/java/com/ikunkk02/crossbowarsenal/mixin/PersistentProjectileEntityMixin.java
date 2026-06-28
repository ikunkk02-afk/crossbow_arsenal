package com.ikunkk02.crossbowarsenal.mixin;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.damage.ModDamageTypes;
import com.ikunkk02.crossbowarsenal.lockon.HomingProjectile;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingProjectileTags;
import com.ikunkk02.crossbowarsenal.util.LockOnMath;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin implements HomingProjectile {
	@Unique
	private static final String HOMING_TARGET_UUID_KEY = "CrossbowArsenalHomingTarget";
	@Unique
	private static final String HOMING_TICKS_KEY = "CrossbowArsenalHomingTicks";
	@Unique
	private static final String HOMING_STRENGTH_KEY = "CrossbowArsenalHomingStrength";
	@Unique
	private static final String HOMING_MAX_DISTANCE_KEY = "CrossbowArsenalHomingMaxDistance";
	@Unique
	private static final String HOMING_ORIGINAL_SPEED_KEY = "CrossbowArsenalHomingOriginalSpeed";

	@Shadow
	protected boolean inGround;
	@Shadow
	protected abstract boolean canHit(Entity entity);
	@Shadow
	protected abstract void onEntityHit(EntityHitResult entityHitResult);

	@Unique
	private UUID crossbow_arsenal$homingTargetUuid;
	@Unique
	private int crossbow_arsenal$homingTicks;
	@Unique
	private double crossbow_arsenal$homingStrength;
	@Unique
	private double crossbow_arsenal$homingMaxDistance;
	@Unique
	private double crossbow_arsenal$homingOriginalSpeed;
	@Unique
	private Vec3d crossbow_arsenal$previousArrowPosition;

	@Override
	public void crossbow_arsenal$setHomingTarget(UUID targetUuid, int homingTicks, double strength, double maxDistance, double originalSpeed) {
		crossbow_arsenal$homingTargetUuid = targetUuid;
		crossbow_arsenal$homingTicks = Math.max(0, homingTicks);
		crossbow_arsenal$homingStrength = LockOnMath.clampHomingStrength(strength);
		crossbow_arsenal$homingMaxDistance = Math.max(1.0D, maxDistance);
		crossbow_arsenal$homingOriginalSpeed = Math.max(0.0D, originalSpeed);
		crossbow_arsenal$previousArrowPosition = null;
		PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
		crossbow_arsenal$debug("Homing target written arrowUuid={} arrowEntityId={} targetUuid={} ticks={} strength={} maxDistance={} originalSpeed={}", projectile.getUuid(), projectile.getId(), targetUuid, crossbow_arsenal$homingTicks, crossbow_arsenal$homingStrength, crossbow_arsenal$homingMaxDistance, crossbow_arsenal$homingOriginalSpeed);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void crossbow_arsenal$tickHoming(CallbackInfo ci) {
		PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
		if (projectile.getWorld().isClient() || crossbow_arsenal$homingTargetUuid == null) {
			return;
		}
		Vec3d currentVelocity = projectile.getVelocity();
		if (inGround) {
			crossbow_arsenal$logTick(projectile, "<missing>", Double.NaN, false, false, false, false, "arrow_in_ground");
			crossbow_arsenal$clearHoming("arrow_in_ground");
			return;
		}
		if (crossbow_arsenal$homingTicks <= 0) {
			crossbow_arsenal$logTick(projectile, "<missing>", Double.NaN, false, false, false, false, "homing_ticks_exhausted");
			crossbow_arsenal$clearHoming("homing_ticks_exhausted");
			return;
		}
		if (!(projectile.getWorld() instanceof ServerWorld serverWorld)) {
			crossbow_arsenal$logTick(projectile, "<missing>", Double.NaN, false, false, false, false, "not_server_world");
			crossbow_arsenal$clearHoming("not_server_world");
			return;
		}

		Entity entity = serverWorld.getEntity(crossbow_arsenal$homingTargetUuid);
		if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
			crossbow_arsenal$logTick(projectile, entity == null ? "<missing>" : entity.getName().getString(), Double.NaN, false, false, false, false, "missing_or_dead_target");
			crossbow_arsenal$clearHoming("missing_or_dead_target");
			return;
		}
		String targetName = target.getName().getString();
		if (!canHit(target) || !target.canBeHitByProjectile()) {
			crossbow_arsenal$logTick(projectile, targetName, Double.NaN, false, false, false, false, "target_cannot_be_hit");
			crossbow_arsenal$clearHoming("target_cannot_be_hit");
			return;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		Vec3d arrowPosition = projectile.getPos();
		Vec3d previousArrowPosition = crossbow_arsenal$previousArrowPosition == null ? arrowPosition : crossbow_arsenal$previousArrowPosition;
		crossbow_arsenal$previousArrowPosition = arrowPosition;
		Vec3d aimPoint = LockOnTargeting.getHomingAimPoint(target);
		double distance = arrowPosition.distanceTo(aimPoint);
		double allowedDistance = crossbow_arsenal$homingMaxDistance + 8.0D;
		if (distance > allowedDistance) {
			crossbow_arsenal$logTick(projectile, targetName, distance, false, false, false, false, "target_too_far");
			crossbow_arsenal$clearHoming("target_too_far");
			return;
		}

		double speed = currentVelocity.length();
		boolean terminalHomingActive = config.enableGuaranteedHomingHit && distance <= config.terminalHomingRadius;
		Vec3d steeringAimPoint = terminalHomingActive
				? aimPoint
				: LockOnMath.predictAimPoint(arrowPosition, aimPoint, target.getVelocity(), speed, config.homingGravityCompensation);
		Vec3d toTarget = steeringAimPoint.subtract(arrowPosition);
		Vec3d desiredDirection = toTarget.normalize();
		Vec3d adjustedVelocity = currentVelocity;
		if (toTarget.lengthSquared() > 1.0E-6D) {
			adjustedVelocity = terminalHomingActive
					? LockOnMath.steerTerminalVelocity(currentVelocity, desiredDirection, config.terminalHomingStrength, 1.5D)
					: LockOnMath.steerVelocity(currentVelocity, desiredDirection, crossbow_arsenal$homingStrength);
			if (adjustedVelocity.lengthSquared() > 1.0E-6D) {
				projectile.setVelocity(adjustedVelocity);
				projectile.setYaw((float) (MathHelper.atan2(adjustedVelocity.x, adjustedVelocity.z) * 57.2957763671875D));
				projectile.setPitch((float) (MathHelper.atan2(adjustedVelocity.y, adjustedVelocity.horizontalLength()) * 57.2957763671875D));
				projectile.velocityModified = true;
			}
		}

		Vec3d nextArrowPosition = arrowPosition.add(adjustedVelocity);
		Box expandedHitbox = target.getBoundingBox().expand(config.homingHitboxExpansion);
		Optional<Vec3d> hitPosition = LockOnMath.getBoxIntersection(expandedHitbox, previousArrowPosition, arrowPosition);
		if (hitPosition.isEmpty()) {
			hitPosition = LockOnMath.getBoxIntersection(expandedHitbox, arrowPosition, nextArrowPosition);
		}
		boolean expandedHitboxIntersected = hitPosition.isPresent();
		boolean clearPathPassed = config.enableGuaranteedHomingHit
				&& expandedHitboxIntersected
				&& (!config.requireClearPathForGuaranteedHit || crossbow_arsenal$hasClearPath(projectile, arrowPosition, aimPoint));
		if (config.enableGuaranteedHomingHit && expandedHitboxIntersected && clearPathPassed) {
			crossbow_arsenal$clearHoming();
			crossbow_arsenal$logTick(projectile, targetName, distance, terminalHomingActive, true, true, true, "forced_hit_triggered");
			onEntityHit(new EntityHitResult(target, hitPosition.orElse(arrowPosition)));
			ci.cancel();
			return;
		}

		if (toTarget.lengthSquared() <= 1.0E-6D || adjustedVelocity.lengthSquared() <= 1.0E-6D) {
			crossbow_arsenal$logTick(projectile, targetName, distance, terminalHomingActive, expandedHitboxIntersected, clearPathPassed, false, "invalid_velocity_or_target_vector");
			crossbow_arsenal$clearHoming("invalid_velocity_or_target_vector");
			return;
		}

		crossbow_arsenal$homingTicks--;
		crossbow_arsenal$logTick(projectile, targetName, distance, terminalHomingActive, expandedHitboxIntersected, clearPathPassed, false, terminalHomingActive ? "terminal_homing_applied" : "normal_homing_applied");
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void crossbow_arsenal$writeHomingNbt(NbtCompound nbt, CallbackInfo ci) {
		if (crossbow_arsenal$homingTargetUuid != null && crossbow_arsenal$homingTicks > 0) {
			nbt.putUuid(HOMING_TARGET_UUID_KEY, crossbow_arsenal$homingTargetUuid);
			nbt.putInt(HOMING_TICKS_KEY, crossbow_arsenal$homingTicks);
			nbt.putDouble(HOMING_STRENGTH_KEY, crossbow_arsenal$homingStrength);
			nbt.putDouble(HOMING_MAX_DISTANCE_KEY, crossbow_arsenal$homingMaxDistance);
			nbt.putDouble(HOMING_ORIGINAL_SPEED_KEY, crossbow_arsenal$homingOriginalSpeed);
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void crossbow_arsenal$readHomingNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.containsUuid(HOMING_TARGET_UUID_KEY)) {
			crossbow_arsenal$homingTargetUuid = nbt.getUuid(HOMING_TARGET_UUID_KEY);
			crossbow_arsenal$homingTicks = nbt.getInt(HOMING_TICKS_KEY);
			crossbow_arsenal$homingStrength = LockOnMath.clampHomingStrength(nbt.getDouble(HOMING_STRENGTH_KEY));
			crossbow_arsenal$homingMaxDistance = Math.max(1.0D, nbt.getDouble(HOMING_MAX_DISTANCE_KEY));
			crossbow_arsenal$homingOriginalSpeed = Math.max(0.0D, nbt.getDouble(HOMING_ORIGINAL_SPEED_KEY));
		} else {
			crossbow_arsenal$clearHoming();
		}
	}

	@Unique
	private void crossbow_arsenal$clearHoming() {
		crossbow_arsenal$homingTargetUuid = null;
		crossbow_arsenal$homingTicks = 0;
		crossbow_arsenal$homingStrength = 0.0D;
		crossbow_arsenal$homingMaxDistance = 0.0D;
		crossbow_arsenal$homingOriginalSpeed = 0.0D;
		crossbow_arsenal$previousArrowPosition = null;
	}

	@Unique
	private void crossbow_arsenal$clearHoming(String reason) {
		UUID previousTarget = crossbow_arsenal$homingTargetUuid;
		int previousTicks = crossbow_arsenal$homingTicks;
		crossbow_arsenal$clearHoming();
		if (previousTarget != null) {
			crossbow_arsenal$debug("Cleared homing projectile={} target={} reason={} remainingTicks={}", ((PersistentProjectileEntity) (Object) this).getUuid(), previousTarget, reason, previousTicks);
		}
	}

	@Unique
	private void crossbow_arsenal$debug(String message, Object... args) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info("[Lock-on] " + message, args);
		}
	}

	@Unique
	private boolean crossbow_arsenal$hasClearPath(PersistentProjectileEntity projectile, Vec3d start, Vec3d aimPoint) {
		HitResult blockHit = projectile.getWorld().raycast(new RaycastContext(
				start,
				aimPoint,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				projectile
		));
		return blockHit.getType() == HitResult.Type.MISS;
	}

	@Unique
	private void crossbow_arsenal$logTick(PersistentProjectileEntity projectile, String targetName, double distance, boolean terminalHomingActive, boolean expandedHitboxIntersected, boolean clearPathPassed, boolean forcedHitTriggered, String result) {
		crossbow_arsenal$debug(
				"Homing tick arrowUuid={} arrowEntityId={} targetName={} distance={} terminalHomingActive={} expandedHitboxIntersected={} clearPathPassed={} forcedHitTriggered={} homingTicksLeft={} velocity={} result={}",
				projectile.getUuid(), projectile.getId(), targetName, distance, terminalHomingActive, expandedHitboxIntersected,
				clearPathPassed, forcedHitTriggered, crossbow_arsenal$homingTicks, projectile.getVelocity(), result
		);
	}

	@Redirect(
			method = "onEntityHit",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/damage/DamageSources;arrow(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;"
			)
	)
	private DamageSource crossbow_arsenal$useRepeatingArrowDamageType(DamageSources damageSources, PersistentProjectileEntity projectile, Entity attacker) {
		if (((Entity) (Object) this).getCommandTags().contains(RepeatingProjectileTags.REPEATING_ARROW)) {
			return damageSources.create(ModDamageTypes.REPEATING_ARROW, projectile, attacker);
		}
		return damageSources.arrow(projectile, attacker);
	}
}
