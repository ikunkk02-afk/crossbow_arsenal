package com.ikunkk02.crossbowarsenal.mixin;

import com.ikunkk02.crossbowarsenal.lockon.HomingArrowUtil;
import com.ikunkk02.crossbowarsenal.penetration.PenetrationArrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponItemMixin {
	@Redirect(
			method = "shootAll",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
			)
	)
	private boolean crossbow_arsenal$applyHomingBeforeSpawn(
			ServerWorld spawnWorld,
			Entity entity,
			ServerWorld world,
			LivingEntity shooter,
			Hand hand,
			ItemStack weaponStack,
			List<ItemStack> projectileStacks,
			float speed,
			float divergence,
			boolean critical,
			LivingEntity target
	) {
		if (shooter instanceof ServerPlayerEntity player && entity instanceof PersistentProjectileEntity projectile) {
			if ((Object) this instanceof CrossbowItem) {
				HomingArrowUtil.applyHomingIfPossible(player, weaponStack, projectile);
			}
			PenetrationArrowUtil.applyPenetrationIfPossible(player, projectile);
		}
		return spawnWorld.spawnEntity(entity);
	}
}
