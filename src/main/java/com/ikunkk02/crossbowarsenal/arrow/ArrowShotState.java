package com.ikunkk02.crossbowarsenal.arrow;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.UUID;

public final class ArrowShotState {
	private static final String INITIALIZED_KEY = "Initialized";
	private static final String ARROW_TYPE_KEY = "ArrowType";
	private static final String PIERCING_LEVEL_KEY = "PiercingLevel";
	private static final String EXPLOSIVE_LEVEL_KEY = "ExplosiveLevel";
	private static final String EXPLOSION_POWER_KEY = "ExplosionPower";
	private static final String CAN_EXPLODE_KEY = "CanExplode";
	private static final String HAS_EXPLODED_KEY = "HasExploded";
	private static final String BREAK_BLOCKS_KEY = "BreakBlocks";
	private static final String CREATE_FIRE_KEY = "CreateFire";
	private static final String KNOCKBACK_MULTIPLIER_KEY = "KnockbackMultiplier";
	private static final String SELF_DAMAGE_MULTIPLIER_KEY = "SelfDamageMultiplier";
	private static final String SHOOTER_UUID_KEY = "ShooterUuid";

	private boolean initialized;
	private ArrowType arrowType = ArrowType.NORMAL;
	private int piercingLevel;
	private int explosiveLevel;
	private double explosionPower;
	private boolean canExplode;
	private boolean hasExploded;
	private boolean breakBlocks;
	private boolean createFire;
	private double knockbackMultiplier = 1.0D;
	private double selfDamageMultiplier = 1.0D;
	private UUID shooterUuid;

	public void initialize(
			ArrowType arrowType,
			int piercingLevel,
			int explosiveLevel,
			double explosionPower,
			boolean canExplode,
			boolean breakBlocks,
			boolean createFire,
			double knockbackMultiplier,
			double selfDamageMultiplier,
			UUID shooterUuid
	) {
		this.initialized = true;
		this.arrowType = arrowType == null ? ArrowType.NORMAL : arrowType;
		this.piercingLevel = Math.max(0, piercingLevel);
		this.explosiveLevel = Math.max(0, explosiveLevel);
		this.explosionPower = sanitizeNonNegative(explosionPower);
		this.canExplode = canExplode && this.explosionPower > 0.0D;
		this.hasExploded = false;
		this.breakBlocks = breakBlocks;
		this.createFire = createFire;
		this.knockbackMultiplier = sanitizeMultiplier(knockbackMultiplier);
		this.selfDamageMultiplier = sanitizeMultiplier(selfDamageMultiplier);
		this.shooterUuid = shooterUuid;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public ArrowType getArrowType() {
		return arrowType;
	}

	public int getPiercingLevel() {
		return piercingLevel;
	}

	public int getExplosiveLevel() {
		return explosiveLevel;
	}

	public double getExplosionPower() {
		return explosionPower;
	}

	public boolean canExplode() {
		return canExplode;
	}

	public boolean hasExploded() {
		return hasExploded;
	}

	public boolean shouldBreakBlocks() {
		return breakBlocks;
	}

	public boolean shouldCreateFire() {
		return createFire;
	}

	public double getKnockbackMultiplier() {
		return knockbackMultiplier;
	}

	public double getSelfDamageMultiplier() {
		return selfDamageMultiplier;
	}

	public UUID getShooterUuid() {
		return shooterUuid;
	}

	public boolean tryMarkExploded() {
		if (!canExplode || hasExploded) {
			return false;
		}
		hasExploded = true;
		return true;
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putBoolean(INITIALIZED_KEY, initialized);
		nbt.putString(ARROW_TYPE_KEY, arrowType.getId());
		nbt.putInt(PIERCING_LEVEL_KEY, piercingLevel);
		nbt.putInt(EXPLOSIVE_LEVEL_KEY, explosiveLevel);
		nbt.putDouble(EXPLOSION_POWER_KEY, explosionPower);
		nbt.putBoolean(CAN_EXPLODE_KEY, canExplode);
		nbt.putBoolean(HAS_EXPLODED_KEY, hasExploded);
		nbt.putBoolean(BREAK_BLOCKS_KEY, breakBlocks);
		nbt.putBoolean(CREATE_FIRE_KEY, createFire);
		nbt.putDouble(KNOCKBACK_MULTIPLIER_KEY, knockbackMultiplier);
		nbt.putDouble(SELF_DAMAGE_MULTIPLIER_KEY, selfDamageMultiplier);
		if (shooterUuid != null) {
			nbt.putUuid(SHOOTER_UUID_KEY, shooterUuid);
		}
	}

	public void readNbt(NbtCompound nbt) {
		initialized = nbt.getBoolean(INITIALIZED_KEY);
		arrowType = ArrowType.fromId(nbt.getString(ARROW_TYPE_KEY));
		piercingLevel = Math.max(0, nbt.getInt(PIERCING_LEVEL_KEY));
		explosiveLevel = Math.max(0, nbt.getInt(EXPLOSIVE_LEVEL_KEY));
		explosionPower = nbt.contains(EXPLOSION_POWER_KEY, NbtElement.DOUBLE_TYPE)
				? sanitizeNonNegative(nbt.getDouble(EXPLOSION_POWER_KEY)) : 0.0D;
		canExplode = nbt.getBoolean(CAN_EXPLODE_KEY) && explosionPower > 0.0D;
		hasExploded = nbt.getBoolean(HAS_EXPLODED_KEY);
		breakBlocks = nbt.getBoolean(BREAK_BLOCKS_KEY);
		createFire = nbt.getBoolean(CREATE_FIRE_KEY);
		knockbackMultiplier = nbt.contains(KNOCKBACK_MULTIPLIER_KEY, NbtElement.DOUBLE_TYPE)
				? sanitizeMultiplier(nbt.getDouble(KNOCKBACK_MULTIPLIER_KEY)) : 1.0D;
		selfDamageMultiplier = nbt.contains(SELF_DAMAGE_MULTIPLIER_KEY, NbtElement.DOUBLE_TYPE)
				? sanitizeMultiplier(nbt.getDouble(SELF_DAMAGE_MULTIPLIER_KEY)) : 1.0D;
		shooterUuid = nbt.containsUuid(SHOOTER_UUID_KEY) ? nbt.getUuid(SHOOTER_UUID_KEY) : null;
	}

	private static double sanitizeNonNegative(double value) {
		return Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D;
	}

	private static double sanitizeMultiplier(double value) {
		return Double.isFinite(value) ? Math.max(0.0D, Math.min(10.0D, value)) : 1.0D;
	}
}
