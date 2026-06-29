package com.ikunkk02.crossbowarsenal.arrow;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ExplosionArrowUtil {
	private ExplosionArrowUtil() {
	}

	public static boolean tryExplode(PersistentProjectileEntity projectile, Vec3d hitPosition) {
		if (!(projectile.getWorld() instanceof ServerWorld serverWorld)
				|| !(projectile instanceof ArrowShotProjectile shotProjectile)) {
			return false;
		}
		ArrowShotState state = shotProjectile.crossbow_arsenal$getArrowShotState();
		if (!state.tryMarkExploded()) {
			return false;
		}

		Vec3d position = hitPosition == null ? projectile.getPos() : hitPosition;
		serverWorld.createExplosion(
				projectile, position.x, position.y, position.z, (float) state.getExplosionPower(),
				state.shouldCreateFire(),
				state.shouldBreakBlocks() ? World.ExplosionSourceType.TNT : World.ExplosionSourceType.NONE
		);
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (config.showPenetrationDebug || config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info(
					"[Special Arrow] Exploded arrowUuid={} type={} power={} position={} breakBlocks={} fire={}",
					projectile.getUuid(), state.getArrowType().getId(), state.getExplosionPower(), position,
					state.shouldBreakBlocks(), state.shouldCreateFire()
			);
		}
		projectile.discard();
		return true;
	}
}
