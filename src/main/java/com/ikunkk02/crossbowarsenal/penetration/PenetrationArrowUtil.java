package com.ikunkk02.crossbowarsenal.penetration;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.arrow.ArrowShotProjectile;
import com.ikunkk02.crossbowarsenal.arrow.ArrowShotState;
import com.ikunkk02.crossbowarsenal.arrow.ArrowType;
import com.ikunkk02.crossbowarsenal.arrow.SpecialArrowRules;
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
		ArrowShotState shotState = projectile instanceof ArrowShotProjectile shotProjectile
				? shotProjectile.crossbow_arsenal$getArrowShotState() : null;
		ArrowType arrowType = shotState == null ? ArrowType.NORMAL : shotState.getArrowType();
		boolean specialPenetrating = arrowType == ArrowType.PENETRATING && config.enablePenetratingArrow;
		boolean canGlass;
		boolean canFragile;
		boolean canSoft = false;
		boolean canWooden = false;
		boolean glassConsumes;
		int maxFragile;
		int maxEntities;
		if (SpecialArrowRules.isExplosiveArrowActive(arrowType, config.enableExplosiveArrow)) {
			canGlass = false;
			canFragile = false;
			glassConsumes = true;
			maxFragile = 0;
			maxEntities = 0;
		} else if (specialPenetrating) {
			canGlass = SpecialArrowRules.canPenetrateFragile(arrowType, true, config.penetratingArrowCanPenetrateFragileBlocks);
			canFragile = canGlass;
			canSoft = SpecialArrowRules.canPenetrateSoft(
					arrowType, true, pierceLevel, config.penetratingArrowSoftBlockRequiresPiercingLevel
			);
			canWooden = SpecialArrowRules.canPenetrateWooden(
					arrowType, true, pierceLevel, config.penetratingArrowWoodBlockRequiresPiercingLevel
			);
			glassConsumes = true;
			maxFragile = SpecialArrowRules.getPenetratingArrowMaxBlocks(pierceLevel, config.maxFragileBlocksPenetrated);
			maxEntities = config.entityPenetrationEnabled
					? PenetrationRules.getMaxEntityTargets(pierceLevel, config.maxEntityPenetrations) : 0;
		} else {
			canGlass = PenetrationRules.canPenetrateGlass(
					config.enableGlassPenetration, homing, config.lockOnArrowCanPenetrateGlass
			);
			canFragile = PenetrationRules.canPenetrateFragile(
					config.fragileBlockPenetrationEnabled, pierceLevel, homing, config.lockOnArrowCanPenetrateFragileBlocks
			);
			glassConsumes = config.glassPenetrationConsumesDurability;
			maxFragile = PenetrationRules.getMaxFragileBlocks(pierceLevel, homing, config.maxFragileBlocksPenetrated);
			maxEntities = config.entityPenetrationEnabled
					? PenetrationRules.getMaxEntityTargets(pierceLevel, config.maxEntityPenetrations) : 0;
		}
		penetratingProjectile.crossbow_arsenal$getPenetrationState().initialize(
				canGlass, canFragile, canSoft, canWooden, glassConsumes, maxFragile, maxEntities
		);
		boolean throughWallHomingActive = homing && config.enableOverpoweredTargeting && config.allowHomingThroughWalls;
		boolean overpoweredPenetration = PenetrationRules.canUseOverpoweredPenetration(
				config.enableOverpoweredTargeting, specialPenetrating, throughWallHomingActive
		);
		penetratingProjectile.crossbow_arsenal$getPenetrationState().initializeOverpowered(
				overpoweredPenetration, config.maxOverpoweredBlocksPenetrated
		);
		debug(
				config,
				"Arrow initialized player={} arrowUuid={} arrowType={} pierceLevel={} homing={} canGlass={} canFragile={} canSoft={} canWooden={} maxFragile={} maxEntities={} glassConsumesDurability={} overpoweredPenetration={} maxOverpowered={}",
				player.getName().getString(), projectile.getUuid(), arrowType.getId(), pierceLevel, homing,
				canGlass, canFragile, canSoft, canWooden, maxFragile, maxEntities, glassConsumes,
				overpoweredPenetration, config.maxOverpoweredBlocksPenetrated
		);
	}

	private static void debug(CrossbowArsenalConfig config, String message, Object... arguments) {
		if (config.showPenetrationDebug || config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info("[Penetration] " + message, arguments);
		}
	}
}
