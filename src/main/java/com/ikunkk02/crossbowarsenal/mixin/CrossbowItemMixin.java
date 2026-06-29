package com.ikunkk02.crossbowarsenal.mixin;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.enchantment.ModEnchantments;
import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingProjectileTags;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingCrossbowManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void crossbow_arsenal$controlRepeatingUse(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		ItemStack crossbow = player.getStackInHand(hand);
		if (RepeatingCrossbowManager.shouldBlockUse(player)) {
			cir.setReturnValue(TypedActionResult.consume(crossbow));
			return;
		}

		if (CrossbowArsenalConfigManager.getConfig().enableRepeatingCrossbow
				&& RepeatingCrossbowManager.getValidRepeatingLevel(world, crossbow) > 0
				&& RepeatingCrossbowManager.isRepeatingLoadedArrow(crossbow)) {
			if (!world.isClient) {
				RepeatingCrossbowManager.handleStart((net.minecraft.server.network.ServerPlayerEntity) player);
			}
			cir.setReturnValue(TypedActionResult.consume(crossbow));
		}
	}

	@Inject(method = "usageTick", at = @At("HEAD"), cancellable = true)
	private void crossbow_arsenal$blockVanillaUseTickWhileRepeating(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
		if (RepeatingCrossbowManager.shouldBlockUse(user)) {
			ci.cancel();
		}
	}

	@Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
	private void crossbow_arsenal$blockVanillaReleaseWhileRepeating(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		if (RepeatingCrossbowManager.shouldBlockUse(user)) {
			ci.cancel();
		}
	}

	@Inject(method = "createArrowEntity", at = @At("RETURN"))
	private void crossbow_arsenal$scaleRepeatingDamage(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical, CallbackInfoReturnable<ProjectileEntity> cir) {
		boolean repeating = RepeatingCrossbowManager.isFiring(shooter);
		if (cir.getReturnValue() instanceof PersistentProjectileEntity projectile) {
			if (repeating) {
				projectile.addCommandTag(RepeatingProjectileTags.REPEATING_ARROW);
				projectile.setDamage(projectile.getDamage() * CrossbowArsenalConfigManager.getConfig().repeatingDamageMultiplier);
			}
		}
	}

	@Inject(method = "appendTooltip", at = @At("TAIL"))
	private void crossbow_arsenal$appendLockOnSightTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
		if (LockOnSightItemData.hasLockOnSight(stack)) {
			tooltip.add(Text.translatable("tooltip.crossbow_arsenal.installed_lock_on_sight").formatted(Formatting.GRAY));
		}
		if (stack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesKey(ModEnchantments.EXPLOSIVE))) {
			tooltip.add(Text.translatable("tooltip.crossbow_arsenal.explosive").formatted(Formatting.GRAY));
		}
	}
}
