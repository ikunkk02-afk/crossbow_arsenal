package com.ikunkk02.crossbowarsenal.penetration;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class PenetrationState {
	private static final String INITIALIZED_KEY = "Initialized";
	private static final String CAN_GLASS_KEY = "CanGlass";
	private static final String CAN_FRAGILE_KEY = "CanFragile";
	private static final String GLASS_CONSUMES_KEY = "GlassConsumes";
	private static final String MAX_FRAGILE_KEY = "MaxFragile";
	private static final String MAX_ENTITIES_KEY = "MaxEntities";
	private static final String BLOCK_COUNT_KEY = "BlockCount";
	private static final String BUDGET_USED_KEY = "BudgetUsed";
	private static final String ENTITY_COUNT_KEY = "EntityCount";
	private static final String DAMAGE_MULTIPLIER_KEY = "DamageMultiplier";
	private static final String HIT_UUIDS_KEY = "HitEntityUuids";

	private boolean initialized;
	private boolean canPenetrateGlass;
	private boolean canPenetrateFragile;
	private boolean glassConsumesDurability;
	private int maxFragileBlocks;
	private int maxEntityPenetrations;
	private int penetratedBlockCount;
	private int penetrationBudgetUsed;
	private int penetratedEntityCount;
	private double currentDamageMultiplier = 1.0D;
	private final Set<UUID> hitEntityUuids = new LinkedHashSet<>();

	public void initialize(boolean canGlass, boolean canFragile, boolean glassConsumes, int maxFragile, int maxEntities) {
		initialized = true;
		canPenetrateGlass = canGlass;
		canPenetrateFragile = canFragile;
		glassConsumesDurability = glassConsumes;
		maxFragileBlocks = Math.max(0, maxFragile);
		maxEntityPenetrations = Math.max(0, maxEntities);
		penetratedBlockCount = 0;
		penetrationBudgetUsed = 0;
		penetratedEntityCount = 0;
		currentDamageMultiplier = 1.0D;
		hitEntityUuids.clear();
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean canPenetrateGlass() {
		return canPenetrateGlass && (!glassConsumesDurability || hasFragileBudget());
	}

	public boolean canPenetrateFragile() {
		return canPenetrateFragile && hasFragileBudget();
	}

	public boolean isEntityPenetrationEnabled() {
		return maxEntityPenetrations > 0;
	}

	public void recordBlockPenetration(boolean glass, double damageMultiplier) {
		penetratedBlockCount++;
		if (!glass || glassConsumesDurability) {
			penetrationBudgetUsed++;
		}
		currentDamageMultiplier *= clampMultiplier(damageMultiplier);
	}

	public boolean recordEntityHit(UUID entityUuid, double damageDecay) {
		if (entityUuid == null || hitEntityUuids.contains(entityUuid) || penetratedEntityCount >= maxEntityPenetrations) {
			return false;
		}
		hitEntityUuids.add(entityUuid);
		penetratedEntityCount++;
		currentDamageMultiplier *= clampMultiplier(damageDecay);
		return true;
	}

	public boolean hasHitEntity(UUID entityUuid) {
		return entityUuid != null && hitEntityUuids.contains(entityUuid);
	}

	public boolean hasReachedEntityLimit() {
		return isEntityPenetrationEnabled() && penetratedEntityCount >= maxEntityPenetrations;
	}

	public int getPenetratedBlockCount() {
		return penetratedBlockCount;
	}

	public int getPenetrationBudgetUsed() {
		return penetrationBudgetUsed;
	}

	public int getPenetratedEntityCount() {
		return penetratedEntityCount;
	}

	public int getMaxFragileBlocks() {
		return maxFragileBlocks;
	}

	public int getMaxEntityPenetrations() {
		return maxEntityPenetrations;
	}

	public double getCurrentDamageMultiplier() {
		return currentDamageMultiplier;
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putBoolean(INITIALIZED_KEY, initialized);
		nbt.putBoolean(CAN_GLASS_KEY, canPenetrateGlass);
		nbt.putBoolean(CAN_FRAGILE_KEY, canPenetrateFragile);
		nbt.putBoolean(GLASS_CONSUMES_KEY, glassConsumesDurability);
		nbt.putInt(MAX_FRAGILE_KEY, maxFragileBlocks);
		nbt.putInt(MAX_ENTITIES_KEY, maxEntityPenetrations);
		nbt.putInt(BLOCK_COUNT_KEY, penetratedBlockCount);
		nbt.putInt(BUDGET_USED_KEY, penetrationBudgetUsed);
		nbt.putInt(ENTITY_COUNT_KEY, penetratedEntityCount);
		nbt.putDouble(DAMAGE_MULTIPLIER_KEY, currentDamageMultiplier);
		NbtList hitUuids = new NbtList();
		for (UUID uuid : hitEntityUuids) {
			hitUuids.add(NbtHelper.fromUuid(uuid));
		}
		nbt.put(HIT_UUIDS_KEY, hitUuids);
	}

	public void readNbt(NbtCompound nbt) {
		initialized = nbt.getBoolean(INITIALIZED_KEY);
		canPenetrateGlass = nbt.getBoolean(CAN_GLASS_KEY);
		canPenetrateFragile = nbt.getBoolean(CAN_FRAGILE_KEY);
		glassConsumesDurability = nbt.getBoolean(GLASS_CONSUMES_KEY);
		maxFragileBlocks = Math.max(0, nbt.getInt(MAX_FRAGILE_KEY));
		maxEntityPenetrations = Math.max(0, nbt.getInt(MAX_ENTITIES_KEY));
		penetratedBlockCount = Math.max(0, nbt.getInt(BLOCK_COUNT_KEY));
		penetrationBudgetUsed = Math.max(0, nbt.getInt(BUDGET_USED_KEY));
		penetratedEntityCount = Math.max(0, nbt.getInt(ENTITY_COUNT_KEY));
		currentDamageMultiplier = nbt.contains(DAMAGE_MULTIPLIER_KEY, NbtElement.DOUBLE_TYPE)
				? Math.max(0.0D, nbt.getDouble(DAMAGE_MULTIPLIER_KEY))
				: 1.0D;
		hitEntityUuids.clear();
		NbtList hitUuids = nbt.getList(HIT_UUIDS_KEY, NbtElement.INT_ARRAY_TYPE);
		for (int index = 0; index < hitUuids.size(); index++) {
			try {
				hitEntityUuids.add(NbtHelper.toUuid(hitUuids.get(index)));
			} catch (IllegalArgumentException ignored) {
				// Ignore malformed UUID entries instead of invalidating the whole arrow.
			}
		}
	}

	private boolean hasFragileBudget() {
		return penetrationBudgetUsed < maxFragileBlocks;
	}

	private static double clampMultiplier(double value) {
		if (!Double.isFinite(value)) {
			return 1.0D;
		}
		return Math.max(0.0D, Math.min(1.0D, value));
	}
}
