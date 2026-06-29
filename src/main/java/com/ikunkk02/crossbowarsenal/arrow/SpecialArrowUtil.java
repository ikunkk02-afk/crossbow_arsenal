package com.ikunkk02.crossbowarsenal.arrow;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.enchantment.ModEnchantments;
import com.ikunkk02.crossbowarsenal.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public final class SpecialArrowUtil {
	private SpecialArrowUtil() {
	}

	public static void applyShotProfile(ServerWorld world, LivingEntity shooter, ItemStack weaponStack, PersistentProjectileEntity projectile) {
		if (!(projectile instanceof ArrowShotProjectile shotProjectile)) {
			return;
		}

		ArrowType arrowType = getArrowType(projectile.getItemStack());
		int piercingLevel = Byte.toUnsignedInt(projectile.getPierceLevel());
		int explosiveLevel = ModEnchantments.getExplosiveLevel(world, weaponStack);
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		double explosionPower = ExplosionRules.calculatePower(
				arrowType, config.enableExplosiveArrow, explosiveLevel,
				config.explosiveArrowBasePower, config.explosiveEnchantLevel1Power,
				config.explosiveEnchantLevel2Power, config.explosiveEnchantLevel3Power,
				config.maxExplosionPower
		);
		boolean breakBlocks = ExplosionRules.shouldBreakBlocks(
				arrowType, config.enableExplosiveArrow, explosiveLevel,
				config.explosiveArrowBreakBlocks, config.explosiveEnchantBreakBlocks
		);
		boolean createFire = ExplosionRules.shouldCreateFire(
				arrowType, config.enableExplosiveArrow, config.explosiveArrowFire
		);
		shotProjectile.crossbow_arsenal$getArrowShotState().initialize(
				arrowType, piercingLevel, explosiveLevel, explosionPower, explosionPower > 0.0D,
				breakBlocks, createFire, config.explosiveArrowKnockbackMultiplier,
				config.explosiveArrowSelfDamageMultiplier, shooter.getUuid()
		);
		if (config.showPenetrationDebug || config.showLockOnDebug) {
			Crossbow_arsenal.LOGGER.info(
					"[Special Arrow] Initialized arrowUuid={} type={} piercing={} explosive={} power={} breakBlocks={} fire={}",
					projectile.getUuid(), arrowType.getId(), piercingLevel, explosiveLevel, explosionPower, breakBlocks, createFire
			);
		}
	}

	public static ArrowType getArrowType(ItemStack projectileStack) {
		if (projectileStack.isOf(ModItems.PENETRATING_ARROW)) {
			return ArrowType.PENETRATING;
		}
		if (projectileStack.isOf(ModItems.EXPLOSIVE_ARROW)) {
			return ArrowType.EXPLOSIVE;
		}
		return ArrowType.NORMAL;
	}
}
