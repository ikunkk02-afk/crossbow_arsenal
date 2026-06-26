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
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin implements HomingProjectile {
	@Unique
	private static final String HOMING_TARGET_UUID_KEY = "CrossbowArsenalHomingTarget";
	@Unique
	private static final String HOMING_TICKS_KEY = "CrossbowArsenalHomingTicks";
	@Unique
	private static final String HOMING_STRENGTH_KEY = "CrossbowArsenalHomingStrength";
	@Unique
	private static final String HOMING_MAX_DISTANCE_KEY = "CrossbowArsenalHomingMaxDistance";

	@Shadow
	protected boolean inGround;

	@Unique
	private UUID crossbow_arsenal$homingTargetUuid;
	@Unique
	private int crossbow_arsenal$homingTicks;
	@Unique
	private double crossbow_arsenal$homingStrength;
	@Unique
	private double crossbow_arsenal$homingMaxDistance;

	@Override
	public void crossbow_arsenal$setHomingTarget(UUID targetUuid, int homingTicks, double strength, double maxDistance) {
		crossbow_arsenal$homingTargetUuid = targetUuid;
		crossbow_arsenal$homingTicks = Math.max(0, homingTicks);
		crossbow_arsenal$homingStrength = LockOnMath.clampHomingStrength(strength);
		crossbow_arsenal$homingMaxDistance = Math.max(1.0D, maxDistance);
		crossbow_arsenal$debug("Homing target written projectile={} target={} ticks={} strength={} maxDistance={}", ((PersistentProjectileEntity) (Object) this).getUuid(), targetUuid, crossbow_arsenal$homingTicks, crossbow_arsenal$homingStrength, crossbow_arsenal$homingMaxDistance);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void crossbow_arsenal$tickHoming(CallbackInfo ci) {
		PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
		if (projectile.getWorld().isClient() || crossbow_arsenal$homingTargetUuid == null) {
			return;
		}
		if (inGround || crossbow_arsenal$homingTicks <= 0 || !projectile.canHit() || !(projectile.getWorld() instanceof ServerWorld serverWorld)) {
			crossbow_arsenal$clearHoming("inactive_projectile");
			return;
		}

		Entity entity = serverWorld.getEntity(crossbow_arsenal$homingTargetUuid);
		if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
			crossbow_arsenal$clearHoming("missing_or_dead_target");
			return;
		}

		Vec3d targetPoint = LockOnTargeting.getHomingTargetPoint(target);
		if (projectile.getPos().squaredDistanceTo(targetPoint) > crossbow_arsenal$homingMaxDistance * crossbow_arsenal$homingMaxDistance) {
			crossbow_arsenal$clearHoming("target_too_far");
			return;
		}

		Vec3d velocity = projectile.getVelocity();
		double speed = velocity.length();
		Vec3d toTarget = targetPoint.subtract(projectile.getPos());
		if (speed < 0.05D || toTarget.lengthSquared() <= 1.0E-6D) {
			crossbow_arsenal$clearHoming("invalid_velocity_or_target_vector");
			return;
		}

		Vec3d desired = toTarget.normalize().multiply(speed);
		Vec3d adjusted = velocity.lerp(desired, crossbow_arsenal$homingStrength);
		if (adjusted.lengthSquared() <= 1.0E-6D) {
			crossbow_arsenal$clearHoming("invalid_adjusted_velocity");
			return;
		}

		projectile.setVelocity(adjusted.normalize().multiply(speed));
		projectile.velocityModified = true;
		crossbow_arsenal$homingTicks--;
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void crossbow_arsenal$writeHomingNbt(NbtCompound nbt, CallbackInfo ci) {
		if (crossbow_arsenal$homingTargetUuid != null && crossbow_arsenal$homingTicks > 0) {
			nbt.putUuid(HOMING_TARGET_UUID_KEY, crossbow_arsenal$homingTargetUuid);
			nbt.putInt(HOMING_TICKS_KEY, crossbow_arsenal$homingTicks);
			nbt.putDouble(HOMING_STRENGTH_KEY, crossbow_arsenal$homingStrength);
			nbt.putDouble(HOMING_MAX_DISTANCE_KEY, crossbow_arsenal$homingMaxDistance);
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void crossbow_arsenal$readHomingNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.containsUuid(HOMING_TARGET_UUID_KEY)) {
			crossbow_arsenal$homingTargetUuid = nbt.getUuid(HOMING_TARGET_UUID_KEY);
			crossbow_arsenal$homingTicks = nbt.getInt(HOMING_TICKS_KEY);
			crossbow_arsenal$homingStrength = LockOnMath.clampHomingStrength(nbt.getDouble(HOMING_STRENGTH_KEY));
			crossbow_arsenal$homingMaxDistance = Math.max(1.0D, nbt.getDouble(HOMING_MAX_DISTANCE_KEY));
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
