package com.ikunkk02.crossbowarsenal.repeating;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RepeatingCrossbowManager {
	private static final int RELEASE_COOLDOWN_TICKS = 3;
	private static final float REPEATING_ARROW_SPEED = 3.15F;
	private static final Map<UUID, Session> SESSIONS = new HashMap<>();

	private RepeatingCrossbowManager() {
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(RepeatingCrossbowManager::tick);
	}

	public static RepeatingCrossbowState getState(LivingEntity user) {
		Session session = SESSIONS.get(user.getUuid());
		return session == null ? RepeatingCrossbowState.IDLE : session.state;
	}

	public static boolean isFiring(LivingEntity user) {
		return getState(user) == RepeatingCrossbowState.FIRING;
	}

	public static boolean shouldBlockUse(LivingEntity user) {
		RepeatingCrossbowState state = getState(user);
		return state == RepeatingCrossbowState.FIRING || state == RepeatingCrossbowState.WAITING_RELEASE;
	}

	public static void handleStart(ServerPlayerEntity player) {
		if (getState(player) != RepeatingCrossbowState.IDLE) {
			return;
		}

		Hand hand = findRepeatingCrossbowHand(player);
		if (hand == null) {
			return;
		}

		ItemStack crossbow = player.getStackInHand(hand);
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		int level = getValidRepeatingLevel(player.getWorld(), crossbow);
		if (!config.enableRepeatingCrossbow || level <= 0 || !isRepeatingLoadedArrow(crossbow)) {
			return;
		}

		SESSIONS.put(player.getUuid(), new Session(RepeatingCrossbowState.FIRING, hand, config.getShotsForLevel(level), 0, player.getWorld().getTime()));
	}

	public static void handleStop(ServerPlayerEntity player) {
		RepeatingCrossbowState state = getState(player);
		if (state == RepeatingCrossbowState.FIRING) {
			enterIdle(player, true);
		} else if (state == RepeatingCrossbowState.WAITING_RELEASE) {
			enterIdle(player, false);
		}
	}

	private static void tick(MinecraftServer server) {
		Iterator<Map.Entry<UUID, Session>> iterator = SESSIONS.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Session> entry = iterator.next();
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
			if (player == null) {
				iterator.remove();
				continue;
			}

			Session session = entry.getValue();
			if (session.state != RepeatingCrossbowState.FIRING) {
				continue;
			}

			tickFiring(player, session);
		}
	}

	private static void tickFiring(ServerPlayerEntity player, Session session) {
		ItemStack crossbow = player.getStackInHand(session.hand);
		CrossbowArsenalConfig config = CrossbowArsenalConfigManager.getConfig();
		if (!config.enableRepeatingCrossbow || crossbow.isEmpty() || getValidRepeatingLevel(player.getWorld(), crossbow) <= 0) {
			enterWaitingRelease(player, session);
			return;
		}

		if (player.getWorld().getTime() < session.nextShotTick) {
			return;
		}

		ItemStack projectile = getProjectileForShot(player, crossbow, session.shotsFired == 0);
		if (projectile.isEmpty()) {
			enterWaitingRelease(player, session);
			return;
		}

		if (session.shotsFired > 0) {
			crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(projectile));
		}

		((CrossbowItem) crossbow.getItem()).shootAll(player.getWorld(), player, session.hand, crossbow, REPEATING_ARROW_SPEED, (float) config.repeatingSpreadMultiplier, null);
		session.shotsFired++;
		session.nextShotTick = player.getWorld().getTime() + config.repeatingDelayTicks;

		if (session.shotsFired >= session.maxShots || crossbow.isEmpty()) {
			crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
			enterWaitingRelease(player, session);
		}
	}

	private static void enterWaitingRelease(ServerPlayerEntity player, Session session) {
		session.state = RepeatingCrossbowState.WAITING_RELEASE;
		addCooldown(player, session.hand);
	}

	private static void enterIdle(ServerPlayerEntity player, boolean addCooldown) {
		Session session = SESSIONS.remove(player.getUuid());
		if (addCooldown && session != null) {
			addCooldown(player, session.hand);
		}
	}

	private static void addCooldown(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(Items.CROSSBOW)) {
			player.getItemCooldownManager().set(stack.getItem(), RELEASE_COOLDOWN_TICKS);
		}
	}

	private static Hand findRepeatingCrossbowHand(ServerPlayerEntity player) {
		ItemStack mainHand = player.getMainHandStack();
		if (getValidRepeatingLevel(player.getWorld(), mainHand) > 0 && isRepeatingLoadedArrow(mainHand)) {
			return Hand.MAIN_HAND;
		}

		ItemStack offHand = player.getOffHandStack();
		if (getValidRepeatingLevel(player.getWorld(), offHand) > 0 && isRepeatingLoadedArrow(offHand)) {
			return Hand.OFF_HAND;
		}

		return null;
	}

	public static int getValidRepeatingLevel(World world, ItemStack crossbow) {
		if (!crossbow.isOf(Items.CROSSBOW)) {
			return 0;
		}
		if (ModEnchantments.hasAny(world, crossbow, Enchantments.MULTISHOT)) {
			return 0;
		}
		return ModEnchantments.getRepeatingLevel(world, crossbow);
	}

	public static boolean isRepeatingLoadedArrow(ItemStack crossbow) {
		ChargedProjectilesComponent chargedProjectiles = crossbow.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
		List<ItemStack> projectiles = chargedProjectiles.getProjectiles();
		return projectiles.size() == 1 && projectiles.get(0).isIn(ItemTags.ARROWS);
	}

	private static ItemStack getProjectileForShot(ServerPlayerEntity player, ItemStack crossbow, boolean firstShot) {
		if (firstShot) {
			ChargedProjectilesComponent chargedProjectiles = crossbow.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
			List<ItemStack> projectiles = chargedProjectiles.getProjectiles();
			return projectiles.size() == 1 && projectiles.get(0).isIn(ItemTags.ARROWS) ? projectiles.get(0) : ItemStack.EMPTY;
		}

		ItemStack arrowStack = findArrow(player);
		if (arrowStack.isEmpty()) {
			if (player.isInCreativeMode()) {
				ItemStack creativeArrow = new ItemStack(Items.ARROW);
				creativeArrow.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
				return creativeArrow;
			}
			return ItemStack.EMPTY;
		}

		if (player.isInCreativeMode()) {
			ItemStack creativeArrow = arrowStack.copyWithCount(1);
			creativeArrow.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
			return creativeArrow;
		}

		ItemStack projectile = arrowStack.split(1);
		if (arrowStack.isEmpty()) {
			player.getInventory().removeOne(arrowStack);
		}
		return projectile;
	}

	private static ItemStack findArrow(PlayerEntity player) {
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			ItemStack stack = player.getInventory().getStack(slot);
			if (stack.isIn(ItemTags.ARROWS)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static final class Session {
		private RepeatingCrossbowState state;
		private final Hand hand;
		private final int maxShots;
		private int shotsFired;
		private long nextShotTick;

		private Session(RepeatingCrossbowState state, Hand hand, int maxShots, int shotsFired, long nextShotTick) {
			this.state = state;
			this.hand = hand;
			this.maxShots = maxShots;
			this.shotsFired = shotsFired;
			this.nextShotTick = nextShotTick;
		}
	}
}
