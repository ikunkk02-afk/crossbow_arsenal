package com.ikunkk02.crossbowarsenal.penetration;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.lockon.HomingProjectile;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PenetrationArrowUtil {
	private PenetrationArrowUtil() {
	}

	public static void applyPenetrationIfPossible(ServerPlayerEntity player, PersistentProjectileEntity projectile) {
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (!(projectile instanceof PenetratingProjectile penetratingProjectile)) {
			debug(config, "Arrow initialization player={} arrowUuid={} result=missing_interface", player.getName().getString(), projectile.getUuid());
			return;
		}

		int pierceLevel = Byte.toUnsignedInt(projectile.getPierceLevel());
		boolean homing = projectile instanceof HomingProjectile homingProjectile && homingProjectile.crossbow_arsenal$isHoming();
		boolean canGlass = PenetrationRules.canPenetrateGlass(
				config.enableGlassPenetration,
				homing,
				config.lockOnArrowCanPenetrateGlass
		);
		boolean canFragile = PenetrationRules.canPenetrateFragile(
				config.fragileBlockPenetrationEnabled,
				pierceLevel,
				homing,
				config.lockOnArrowCanPenetrateFragileBlocks
		);
		int maxFragile = PenetrationRules.getMaxFragileBlocks(pierceLevel, homing, config.maxFragileBlocksPenetrated);
		int maxEntities = config.entityPenetrationEnabled
				? PenetrationRules.getMaxEntityTargets(pierceLevel, config.maxEntityPenetrations)
				: 0;
		penetratingProjectile.crossbow_arsenal$initializePenetration(
				canGlass,
				canFragile,
				config.glassPenetrationConsumesDurability,
				maxFragile,
				maxEntities
		);
		debug(
				config,
				"Arrow initialized player={} arrowUuid={} pierceLevel={} homing={} canGlass={} canFragile={} maxFragile={} maxEntities={} glassConsumesDurability={}",
				player.getName().getString(), projectile.getUuid(), pierceLevel, homing, canGlass, canFragile,
				maxFragile, maxEntities, config.glassPenetrationConsumesDurability
		);
	}

	private static void debug(CrossbowArsenalConfig config, String message, Object... arguments) {
		if (config.showPenetrationDebug || config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info("[Penetration] " + message, arguments);
		}
	}
}
