package com.ikunkk02.crossbowarsenal.component;

import com.ikunkk02.crossbowarsenal.lockon.LockOnManager;
import com.ikunkk02.crossbowarsenal.util.LockOnTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public final class PlayerLockTargetComponent implements LockTargetComponent, ServerTickingComponent {
	private static final String TARGET_UUID_KEY = "LockedTargetUuid";
	private static final String TARGET_ENTITY_ID_KEY = "LockedTargetEntityId";
	private static final String LAST_LOCK_TIME_KEY = "LastLockTime";

	private final @Nullable PlayerEntity player;
	private @Nullable UUID lockedTargetUuid;
	private int lockedTargetEntityId = -1;
	private long lastLockTime;

	public PlayerLockTargetComponent(@Nullable PlayerEntity player) {
		this.player = player;
	}

	@Override
	public @Nullable UUID getLockedTargetUuid() {
		return lockedTargetUuid;
	}

	@Override
	public int getLockedTargetEntityId() {
		return lockedTargetEntityId;
	}

	@Override
	public long getLastLockTime() {
		return lastLockTime;
	}

	@Override
	public void setTarget(UUID targetUuid, int targetEntityId, long lockTime) {
		this.lockedTargetUuid = targetUuid;
		this.lockedTargetEntityId = targetEntityId;
		this.lastLockTime = lockTime;
	}

	@Override
	public void clearTarget() {
		lockedTargetUuid = null;
		lockedTargetEntityId = -1;
		lastLockTime = 0L;
	}

	@Override
	public void serverTick() {
		if (lockedTargetUuid == null || !(player instanceof ServerPlayerEntity serverPlayer)) {
			return;
		}
		if (!LockOnTargeting.hasUsableSightCrossbow(serverPlayer)) {
			LockOnManager.clearTarget(serverPlayer, "player_not_holding_lock_on_crossbow");
			return;
		}
		if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Entity target = serverWorld.getEntity(lockedTargetUuid);
		if (!(target instanceof LivingEntity livingTarget) || !livingTarget.isAlive() || livingTarget.isDead()) {
			LockOnManager.clearTarget(serverPlayer, "target_missing_or_dead");
		}
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (!tag.containsUuid(TARGET_UUID_KEY)) {
			clearTarget();
			return;
		}
		lockedTargetUuid = tag.getUuid(TARGET_UUID_KEY);
		lockedTargetEntityId = tag.getInt(TARGET_ENTITY_ID_KEY);
		lastLockTime = tag.getLong(LAST_LOCK_TIME_KEY);
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (lockedTargetUuid == null) {
			return;
		}
		tag.putUuid(TARGET_UUID_KEY, lockedTargetUuid);
		tag.putInt(TARGET_ENTITY_ID_KEY, lockedTargetEntityId);
		tag.putLong(LAST_LOCK_TIME_KEY, lastLockTime);
	}

}
