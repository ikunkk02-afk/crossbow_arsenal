package com.ikunkk02.crossbowarsenal.client.lockon;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class LockOnTargetFinder {
	private LockOnTargetFinder() {
	}

	public static LivingEntity findTarget(MinecraftClient client) {
		if (client.player == null || client.world == null || !isHoldingSightCrossbowForLock(client)) {
			return null;
		}

		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		Vec3d eye = client.player.getEyePos();
		double maxDistance = config.lockOnMaxDistance;
		Box searchBox = client.player.getBoundingBox().expand(maxDistance);
		LivingEntity best = null;
		Score bestScore = null;
		float tickDelta = 1.0F;

		for (Entity entity : client.world.getOtherEntities(client.player, searchBox, candidate -> candidate instanceof LivingEntity)) {
			if (!(entity instanceof LivingEntity target) || !LockOnTargeting.isValidBaseTarget(client.player, target, maxDistance, config.requireLineOfSight)) {
				continue;
			}

			double crosshairDistance;
			if (config.enableFullScreenLockOn) {
				LockOnScreenProjection.ScreenRect rect = LockOnScreenProjection.projectEntity(client, target, tickDelta);
				if (!LockOnScreenProjection.intersectsScreen(rect, client, config.lockOnScreenMargin)) {
					continue;
				}
				crosshairDistance = LockOnScreenProjection.squaredDistanceToScreenCenter(rect, client);
			} else {
				if (!LockOnTargeting.isWithinForwardAngle(client.player, target, config.lockOnAngleDegrees)) {
					continue;
				}
				Vec3d direction = LockOnTargeting.getTargetPoint(target).subtract(eye).normalize();
				crosshairDistance = 1.0D - client.player.getRotationVec(1.0F).normalize().dotProduct(direction);
			}
			double distanceSquared = eye.squaredDistanceTo(LockOnTargeting.getTargetPoint(target));
			Score score = new Score(LockOnTargeting.getPriority(target), crosshairDistance, distanceSquared);
			if (bestScore == null || score.compareTo(bestScore) < 0) {
				bestScore = score;
				best = target;
			}
		}

		return best;
	}

	public static boolean isHoldingSightCrossbowForLock(MinecraftClient client) {
		ItemStack mainHand = client.player.getMainHandStack();
		ItemStack offHand = client.player.getOffHandStack();
		return LockOnSightItemData.hasLockOnSight(mainHand) || LockOnSightItemData.hasLockOnSight(offHand);
	}

	private record Score(int priority, double crosshairDistance, double distanceSquared) implements Comparable<Score> {
		@Override
		public int compareTo(Score other) {
			int priorityCompare = Integer.compare(priority, other.priority);
			if (priorityCompare != 0) {
				return priorityCompare;
			}
			int crosshairCompare = Double.compare(crosshairDistance, other.crosshairDistance);
			return crosshairCompare != 0 ? crosshairCompare : Double.compare(distanceSquared, other.distanceSquared);
		}
	}
}
