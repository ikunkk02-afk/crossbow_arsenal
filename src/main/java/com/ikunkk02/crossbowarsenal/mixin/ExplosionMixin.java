package com.ikunkk02.crossbowarsenal.mixin;

import com.ikunkk02.crossbowarsenal.arrow.ArrowShotProjectile;
import com.ikunkk02.crossbowarsenal.arrow.ArrowShotState;
import com.ikunkk02.crossbowarsenal.arrow.ExplosionRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Shadow
	@Final
	@Nullable
	private Entity entity;

	@Redirect(
			method = "collectBlocksAndDamageEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	private boolean crossbow_arsenal$scaleArrowExplosionSelfDamage(Entity target, DamageSource damageSource, float damage) {
		ArrowShotState state = crossbow_arsenal$getActiveArrowState();
		boolean shooter = state != null && state.getShooterUuid() != null && state.getShooterUuid().equals(target.getUuid());
		float adjustedDamage = state == null ? damage : ExplosionRules.scaleDamage(shooter, damage, state.getSelfDamageMultiplier());
		return target.damage(damageSource, adjustedDamage);
	}

	@Redirect(
			method = "collectBlocksAndDamageEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
			)
	)
	private void crossbow_arsenal$scaleArrowExplosionKnockback(Entity target, Vec3d resultingVelocity) {
		ArrowShotState state = crossbow_arsenal$getActiveArrowState();
		if (state == null) {
			target.setVelocity(resultingVelocity);
			return;
		}
		Vec3d currentVelocity = target.getVelocity();
		Vec3d explosionDelta = resultingVelocity.subtract(currentVelocity);
		target.setVelocity(currentVelocity.add(ExplosionRules.scaleKnockback(explosionDelta, state.getKnockbackMultiplier())));
	}

	@Redirect(
			method = "collectBlocksAndDamageEntities",
			at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
	)
	private Object crossbow_arsenal$scaleArrowExplosionPlayerPacketKnockback(Map<Object, Object> map, Object key, Object value) {
		ArrowShotState state = crossbow_arsenal$getActiveArrowState();
		if (state != null && value instanceof Vec3d knockback) {
			value = ExplosionRules.scaleKnockback(knockback, state.getKnockbackMultiplier());
		}
		return map.put(key, value);
	}

	private ArrowShotState crossbow_arsenal$getActiveArrowState() {
		if (!(entity instanceof PersistentProjectileEntity projectile)
				|| !(projectile instanceof ArrowShotProjectile shotProjectile)) {
			return null;
		}
		ArrowShotState state = shotProjectile.crossbow_arsenal$getArrowShotState();
		return state.hasExploded() ? state : null;
	}
}
