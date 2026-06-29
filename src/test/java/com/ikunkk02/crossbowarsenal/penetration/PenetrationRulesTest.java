package com.ikunkk02.crossbowarsenal.penetration;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class PenetrationRulesTest {
	private PenetrationRulesTest() {
	}

	public static void main(String[] args) throws Exception {
		Class<?> rulesClass = requireClass("com.ikunkk02.crossbowarsenal.penetration.PenetrationRules");
		Method maxEntities = rulesClass.getMethod("getMaxEntityTargets", int.class, int.class);
		Method maxFragile = rulesClass.getMethod("getMaxFragileBlocks", int.class, boolean.class, int.class);
		Method sufficientSpeed = rulesClass.getMethod("hasSufficientSpeed", double.class);
		Method canGlassRule = rulesClass.getMethod("canPenetrateGlass", boolean.class, boolean.class, boolean.class);
		Method canFragileRule = rulesClass.getMethod("canPenetrateFragile", boolean.class, int.class, boolean.class, boolean.class);
		Method reduceVelocity = rulesClass.getMethod("reduceVelocity", Vec3d.class, double.class);
		Method exitPosition = rulesClass.getMethod("getExitPosition", Vec3d.class, Vec3d.class);
		Method blockFirst = rulesClass.getMethod("isBlockBeforeEntity", Vec3d.class, Vec3d.class, Vec3d.class);
		Method guaranteedHit = rulesClass.getMethod("canTriggerGuaranteedHit", boolean.class, boolean.class, boolean.class);
		Method overpoweredActive = rulesClass.getMethod(
				"canUseOverpoweredPenetration", boolean.class, boolean.class, boolean.class
		);
		Method overpoweredBlock = rulesClass.getMethod(
				"canBreakOverpoweredBlock",
				boolean.class, boolean.class, boolean.class, boolean.class, boolean.class, int.class, int.class
		);

		assertEquals(0, invokeInt(maxEntities, 0, 3), "ordinary arrows do not gain entity penetration");
		assertEquals(2, invokeInt(maxEntities, 1, 3), "Piercing I can hit two targets");
		assertEquals(3, invokeInt(maxEntities, 2, 3), "Piercing II can hit three targets");
		assertEquals(3, invokeInt(maxEntities, 4, 3), "configured entity cap wins over Piercing IV");
		assertEquals(0, invokeInt(maxFragile, 0, false, 3), "ordinary arrows do not gain fragile penetration");
		assertEquals(1, invokeInt(maxFragile, 1, false, 3), "Piercing I gets one fragile block");
		assertEquals(3, invokeInt(maxFragile, 4, false, 3), "fragile penetration obeys configured cap");
		assertEquals(3, invokeInt(maxFragile, 0, true, 3), "homing arrows get the full fragile block cap");
		assertFalse((boolean) sufficientSpeed.invoke(null, 0.199D), "slow arrows must not penetrate");
		assertTrue((boolean) sufficientSpeed.invoke(null, 0.2D), "minimum penetration speed is inclusive");
		assertTrue((boolean) canGlassRule.invoke(null, true, false, false), "ordinary arrows use the global glass switch");
		assertFalse((boolean) canGlassRule.invoke(null, true, true, false), "lock-on glass toggle overrides the ordinary rule");
		assertFalse((boolean) canGlassRule.invoke(null, false, true, true), "global glass switch remains authoritative");
		assertTrue((boolean) canFragileRule.invoke(null, true, 1, false, false), "Piercing arrows can penetrate fragile blocks");
		assertTrue((boolean) canFragileRule.invoke(null, true, 0, true, true), "lock-on arrows can gain fragile penetration");
		assertFalse((boolean) canFragileRule.invoke(null, true, 0, true, false), "lock-on fragile toggle is authoritative without Piercing");
		assertFalse((boolean) canFragileRule.invoke(null, false, 4, true, true), "global fragile switch remains authoritative");
		Vec3d forward = new Vec3d(2.0D, 0.0D, 0.0D);
		assertEquals(new Vec3d(1.7D, 0.0D, 0.0D), reduceVelocity.invoke(null, forward, 0.85D), "speed multiplier preserves direction");
		assertEquals(new Vec3d(1.05D, 2.0D, 3.0D), exitPosition.invoke(null, new Vec3d(1.0D, 2.0D, 3.0D), forward), "arrow moves just beyond the hit face");
		assertTrue((boolean) blockFirst.invoke(null, Vec3d.ZERO, new Vec3d(1.0D, 0.0D, 0.0D), null), "block wins when there is no entity collision");
		assertTrue((boolean) blockFirst.invoke(null, Vec3d.ZERO, new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(2.0D, 0.0D, 0.0D)), "nearer block wins collision ordering");
		assertFalse((boolean) blockFirst.invoke(null, Vec3d.ZERO, new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D)), "nearer entity remains a vanilla entity hit");
		assertTrue((boolean) guaranteedHit.invoke(null, true, true, true), "guaranteed hit requires an enabled, intersecting, clear path");
		assertFalse((boolean) guaranteedHit.invoke(null, true, true, false), "a stone wall always blocks guaranteed hit");
		assertFalse((boolean) overpoweredActive.invoke(null, false, true, true), "normal mode never gains overpowered penetration");
		assertTrue((boolean) overpoweredActive.invoke(null, true, true, false), "overpowered penetrating arrows gain hard-block penetration");
		assertTrue((boolean) overpoweredActive.invoke(null, true, false, true), "authorized through-wall homing uses synchronized block destruction");
		assertFalse((boolean) overpoweredActive.invoke(null, true, false, false), "ordinary non-homing arrows remain unchanged");
		assertTrue((boolean) overpoweredBlock.invoke(null, true, true, false, false, true, 7, 8), "eighth allowed block can be broken");
		assertFalse((boolean) overpoweredBlock.invoke(null, true, true, false, false, true, 8, 8), "overpowered block cap is exclusive");
		assertFalse((boolean) overpoweredBlock.invoke(null, true, true, true, false, true, 0, 8), "never-break tag is absolute");
		assertFalse((boolean) overpoweredBlock.invoke(null, true, true, false, true, true, 0, 8), "block entities are absolute protection");
		assertFalse((boolean) overpoweredBlock.invoke(null, true, true, false, false, false, 0, 8), "disabled stone or wood category remains protected");

		Class<?> projectileInterface = requireClass("com.ikunkk02.crossbowarsenal.penetration.PenetratingProjectile");
		projectileInterface.getMethod("crossbow_arsenal$initializePenetration", boolean.class, boolean.class, boolean.class, int.class, int.class);
		projectileInterface.getMethod("crossbow_arsenal$getPenetrationState");
		Class<?> arrowUtil = requireClass("com.ikunkk02.crossbowarsenal.penetration.PenetrationArrowUtil");
		arrowUtil.getMethod(
				"applyPenetrationIfPossible",
				requireClass("net.minecraft.server.network.ServerPlayerEntity"),
				requireClass("net.minecraft.entity.projectile.PersistentProjectileEntity")
		);

		Class<?> homingInterface = requireClass("com.ikunkk02.crossbowarsenal.lockon.HomingProjectile");
		homingInterface.getMethod("crossbow_arsenal$isHoming");
		homingInterface.getMethod("crossbow_arsenal$weakenHoming", double.class);
		Class<?> projectileMixin = requireClass("com.ikunkk02.crossbowarsenal.mixin.PersistentProjectileEntityMixin");
		projectileMixin.getDeclaredMethod(
				"crossbow_arsenal$recordPenetratingEntityDamage",
				requireClass("net.minecraft.entity.Entity"),
				requireClass("net.minecraft.entity.damage.DamageSource"),
				float.class
		);

		Class<?> stateClass = requireClass("com.ikunkk02.crossbowarsenal.penetration.PenetrationState");
		Constructor<?> constructor = stateClass.getConstructor();
		Object state = constructor.newInstance();
		Method initialize = stateClass.getMethod("initialize", boolean.class, boolean.class, boolean.class, int.class, int.class);
		Method canGlass = stateClass.getMethod("canPenetrateGlass");
		Method canFragile = stateClass.getMethod("canPenetrateFragile");
		Method recordBlock = stateClass.getMethod("recordBlockPenetration", boolean.class, double.class);
		Method recordEntity = stateClass.getMethod("recordEntityHit", UUID.class, double.class);
		Method hasHitEntity = stateClass.getMethod("hasHitEntity", UUID.class);
		Method getBlockCount = stateClass.getMethod("getPenetratedBlockCount");
		Method getEntityCount = stateClass.getMethod("getPenetratedEntityCount");
		Method getMultiplier = stateClass.getMethod("getCurrentDamageMultiplier");
		Method initializeOverpowered = stateClass.getMethod("initializeOverpowered", boolean.class, int.class);
		Method canOverpowered = stateClass.getMethod("canPenetrateOverpowered");
		Method recordOverpowered = stateClass.getMethod("recordOverpoweredBlockPenetration", double.class);
		Method getOverpoweredCount = stateClass.getMethod("getOverpoweredBlockCount");
		Method getMaxOverpowered = stateClass.getMethod("getMaxOverpoweredBlocks");
		Method writeNbt = stateClass.getMethod("writeNbt", NbtCompound.class);
		Method readNbt = stateClass.getMethod("readNbt", NbtCompound.class);

		initialize.invoke(state, true, true, true, 3, 3);
		assertTrue((boolean) canGlass.invoke(state), "glass starts enabled");
		recordBlock.invoke(state, true, 0.9D);
		recordBlock.invoke(state, false, 0.8D);
		assertEquals(2, (int) getBlockCount.invoke(state), "both glass and fragile blocks are counted");
		assertDoubleEquals(0.72D, (double) getMultiplier.invoke(state), "block multipliers compose");
		assertTrue((boolean) canFragile.invoke(state), "one fragile budget remains after two consuming blocks");
		recordBlock.invoke(state, false, 0.8D);
		assertFalse((boolean) canGlass.invoke(state), "glass consumes the shared budget when configured");
		assertFalse((boolean) canFragile.invoke(state), "fragile blocks stop at the shared budget");

		UUID first = UUID.fromString("11111111-1111-1111-1111-111111111111");
		UUID second = UUID.fromString("22222222-2222-2222-2222-222222222222");
		UUID third = UUID.fromString("33333333-3333-3333-3333-333333333333");
		assertTrue((boolean) recordEntity.invoke(state, first, 0.8D), "first entity is recorded");
		assertFalse((boolean) recordEntity.invoke(state, first, 0.8D), "duplicate UUID is rejected");
		assertTrue((boolean) recordEntity.invoke(state, second, 0.8D), "second entity is recorded");
		assertTrue((boolean) recordEntity.invoke(state, third, 0.8D), "third entity is recorded");
		assertFalse((boolean) recordEntity.invoke(state, UUID.randomUUID(), 0.8D), "entity cap rejects a fourth target");
		assertEquals(3, (int) getEntityCount.invoke(state), "only unique successful entity hits are counted");
		assertTrue((boolean) hasHitEntity.invoke(state, second), "hit UUID is retained");
		assertDoubleEquals(0.9D * 0.8D * 0.8D * 0.8D * 0.8D * 0.8D, (double) getMultiplier.invoke(state), "block and entity decay compound after each successful penetration");

		initializeOverpowered.invoke(state, true, 8);
		assertTrue((boolean) canOverpowered.invoke(state), "overpowered budget starts enabled");
		for (int index = 0; index < 8; index++) {
			recordOverpowered.invoke(state, 0.75D);
		}
		assertEquals(8, (int) getOverpoweredCount.invoke(state), "overpowered block count reaches configured cap");
		assertEquals(8, (int) getMaxOverpowered.invoke(state), "overpowered block cap is retained");
		assertFalse((boolean) canOverpowered.invoke(state), "overpowered penetration stops at configured cap");

		NbtCompound nbt = new NbtCompound();
		writeNbt.invoke(state, nbt);
		Object restored = constructor.newInstance();
		readNbt.invoke(restored, nbt);
		assertEquals(11, (int) getBlockCount.invoke(restored), "normal and overpowered block counts contribute to the total");
		assertEquals(3, (int) getEntityCount.invoke(restored), "entity count survives NBT");
		assertEquals(8, (int) getOverpoweredCount.invoke(restored), "overpowered block count survives NBT");
		assertEquals(8, (int) getMaxOverpowered.invoke(restored), "overpowered block cap survives NBT");
		assertTrue((boolean) hasHitEntity.invoke(restored, first), "UUID set survives NBT");
		assertDoubleEquals((double) getMultiplier.invoke(state), (double) getMultiplier.invoke(restored), "damage multiplier survives NBT");

		CrossbowArsenalConfig config = new CrossbowArsenalConfig();
		assertBooleanField(config, "enableGlassPenetration", true);
		assertDoubleField(config, "glassPenetrationDamageMultiplier", 0.9D);
		assertDoubleField(config, "glassPenetrationSpeedMultiplier", 0.92D);
		assertBooleanField(config, "glassPenetrationConsumesDurability", false);
		assertBooleanField(config, "fragileBlockPenetrationEnabled", true);
		assertDoubleField(config, "fragileBlockDamageMultiplier", 0.8D);
		assertDoubleField(config, "fragileBlockSpeedMultiplier", 0.85D);
		assertIntField(config, "maxFragileBlocksPenetrated", 3);
		assertBooleanField(config, "entityPenetrationEnabled", true);
		assertDoubleField(config, "entityPenetrationDamageDecay", 0.8D);
		assertIntField(config, "maxEntityPenetrations", 3);
		assertBooleanField(config, "lockOnArrowCanPenetrateGlass", true);
		assertBooleanField(config, "lockOnArrowCanPenetrateFragileBlocks", true);
		assertBooleanField(config, "showPenetrationDebug", false);
		assertBooleanField(config, "overpoweredPenetrationBreaksStone", true);
		assertBooleanField(config, "overpoweredPenetrationBreaksWood", true);
		assertIntField(config, "maxOverpoweredBlocksPenetrated", 8);
		assertDoubleField(config, "overpoweredHardBlockSpeedMultiplier", 0.75D);
		assertDoubleField(config, "overpoweredHardBlockDamageMultiplier", 0.75D);
	}

	private static Class<?> requireClass(String name) {
		try {
			return Class.forName(name, false, PenetrationRulesTest.class.getClassLoader());
		} catch (ClassNotFoundException exception) {
			throw new AssertionError("Missing production class " + name, exception);
		}
	}

	private static int invokeInt(Method method, Object... arguments) throws Exception {
		return (int) method.invoke(null, arguments);
	}

	private static void assertBooleanField(Object object, String name, boolean expected) throws Exception {
		Field field = object.getClass().getField(name);
		assertEquals(expected, field.getBoolean(object), "config default " + name);
	}

	private static void assertDoubleField(Object object, String name, double expected) throws Exception {
		Field field = object.getClass().getField(name);
		assertDoubleEquals(expected, field.getDouble(object), "config default " + name);
	}

	private static void assertIntField(Object object, String name, int expected) throws Exception {
		Field field = object.getClass().getField(name);
		assertEquals(expected, field.getInt(object), "config default " + name);
	}

	private static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	private static void assertFalse(boolean condition, String message) {
		assertTrue(!condition, message);
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!java.util.Objects.equals(expected, actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}

	private static void assertDoubleEquals(double expected, double actual, String message) {
		if (Math.abs(expected - actual) > 1.0E-9D) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
