package com.ikunkk02.crossbowarsenal.arrow;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class SpecialArrowRulesTest {
	private SpecialArrowRulesTest() {
	}

	public static void main(String[] args) throws Exception {
		Class<?> arrowTypeClass = requireClass("com.ikunkk02.crossbowarsenal.arrow.ArrowType");
		Object normal = enumValue(arrowTypeClass, "NORMAL");
		Object penetrating = enumValue(arrowTypeClass, "PENETRATING");
		Object explosive = enumValue(arrowTypeClass, "EXPLOSIVE");

		Class<?> rulesClass = requireClass("com.ikunkk02.crossbowarsenal.arrow.SpecialArrowRules");
		Method maxBlocks = rulesClass.getMethod("getPenetratingArrowMaxBlocks", int.class, int.class);
		Method fragile = rulesClass.getMethod("canPenetrateFragile", arrowTypeClass, boolean.class, boolean.class);
		Method soft = rulesClass.getMethod("canPenetrateSoft", arrowTypeClass, boolean.class, int.class, int.class);
		Method wooden = rulesClass.getMethod("canPenetrateWooden", arrowTypeClass, boolean.class, int.class, int.class);
		Method explodeBefore = rulesClass.getMethod("shouldExplodeBeforePenetration", arrowTypeClass, boolean.class, boolean.class);
		Method explodeAfter = rulesClass.getMethod("shouldExplodeAfterSuccessfulPenetration", arrowTypeClass, boolean.class, boolean.class);
		Method explosiveArrowActive = rulesClass.getMethod("isExplosiveArrowActive", arrowTypeClass, boolean.class);

		assertEquals(3, maxBlocks.invoke(null, 0, 3), "unenchanted penetrating arrow uses base block budget");
		assertEquals(3, maxBlocks.invoke(null, 3, 3), "Piercing III changes categories, not count");
		assertEquals(4, maxBlocks.invoke(null, 4, 3), "Piercing IV adds one penetration");
		assertEquals(64, maxBlocks.invoke(null, 100, 3), "penetration count is capped");
		assertTrue((boolean) fragile.invoke(null, penetrating, true, true), "penetrating arrows penetrate fragile blocks by default");
		assertFalse((boolean) fragile.invoke(null, penetrating, false, true), "feature toggle disables penetrating-arrow behavior");
		assertFalse((boolean) fragile.invoke(null, normal, true, true), "normal arrows do not use special-arrow fragile rules");
		assertFalse((boolean) soft.invoke(null, penetrating, true, 1, 2), "Piercing I cannot penetrate soft blocks");
		assertTrue((boolean) soft.invoke(null, penetrating, true, 2, 2), "Piercing II penetrates soft blocks");
		assertFalse((boolean) wooden.invoke(null, penetrating, true, 2, 3), "Piercing II cannot penetrate wooden blocks");
		assertTrue((boolean) wooden.invoke(null, penetrating, true, 3, 3), "Piercing III penetrates wooden blocks");
		assertFalse((boolean) wooden.invoke(null, explosive, true, 10, 0), "explosive arrows never gain block penetration");
		assertTrue((boolean) explodeBefore.invoke(null, explosive, true, false), "explosive arrows always explode before penetration");
		assertTrue((boolean) explodeBefore.invoke(null, penetrating, true, true), "configured explosion priority stops penetration");
		assertFalse((boolean) explodeBefore.invoke(null, penetrating, true, false), "configured penetration priority allows one penetration");
		assertTrue((boolean) explodeAfter.invoke(null, penetrating, true, false), "penetrating explosive arrows explode after one successful penetration");
		assertFalse((boolean) explodeAfter.invoke(null, normal, true, false), "normal arrows do not use delayed penetration explosions");
		assertTrue((boolean) explosiveArrowActive.invoke(null, explosive, true), "enabled explosive arrows disable continued penetration");
		assertFalse((boolean) explosiveArrowActive.invoke(null, explosive, false), "disabled explosive arrows fall back to normal arrow rules");

		Class<?> explosionRulesClass = requireClass("com.ikunkk02.crossbowarsenal.arrow.ExplosionRules");
		Method power = explosionRulesClass.getMethod(
				"calculatePower", arrowTypeClass, boolean.class, int.class,
				double.class, double.class, double.class, double.class, double.class
		);
		Method breakBlocks = explosionRulesClass.getMethod(
				"shouldBreakBlocks", arrowTypeClass, boolean.class, int.class, boolean.class, boolean.class
		);
		Method fire = explosionRulesClass.getMethod("shouldCreateFire", arrowTypeClass, boolean.class, boolean.class);
		Method selfDamage = explosionRulesClass.getMethod("scaleDamage", boolean.class, float.class, double.class);
		Method knockback = explosionRulesClass.getMethod("scaleKnockback", Vec3d.class, double.class);

		assertDoubleEquals(0.0D, (double) power.invoke(null, normal, true, 0, 2.0D, 1.5D, 2.2D, 3.0D, 4.0D), "ordinary arrows need the enchantment");
		assertDoubleEquals(1.5D, (double) power.invoke(null, normal, true, 1, 2.0D, 1.5D, 2.2D, 3.0D, 4.0D), "Explosive I uses configured power");
		assertDoubleEquals(2.0D, (double) power.invoke(null, explosive, true, 0, 2.0D, 1.5D, 2.2D, 3.0D, 4.0D), "explosive arrows have base power");
		assertDoubleEquals(4.0D, (double) power.invoke(null, explosive, true, 3, 2.0D, 1.5D, 2.2D, 3.0D, 4.0D), "combined power obeys cap");
		assertDoubleEquals(2.2D, (double) power.invoke(null, explosive, false, 2, 2.0D, 1.5D, 2.2D, 3.0D, 4.0D), "disabled explosive arrow still receives enchantment power");
		assertTrue((boolean) breakBlocks.invoke(null, explosive, true, 0, true, false), "explosive arrow block toggle is honored");
		assertTrue((boolean) breakBlocks.invoke(null, normal, true, 2, false, true), "enchantment block toggle is honored");
		assertFalse((boolean) breakBlocks.invoke(null, explosive, true, 2, false, false), "terrain remains safe when both toggles are false");
		assertTrue((boolean) fire.invoke(null, explosive, true, true), "explosive arrow fire toggle is honored");
		assertFalse((boolean) fire.invoke(null, normal, true, true), "enchantment-only explosions do not create fire");
		assertDoubleEquals(3.5D, ((Float) selfDamage.invoke(null, true, 10.0F, 0.35D)).doubleValue(), "shooter damage is scaled");
		assertDoubleEquals(10.0D, ((Float) selfDamage.invoke(null, false, 10.0F, 0.35D)).doubleValue(), "other targets retain full damage");
		assertEquals(new Vec3d(1.5D, 3.0D, 4.5D), knockback.invoke(null, new Vec3d(1.0D, 2.0D, 3.0D), 1.5D), "knockback vector is scaled");

		Class<?> stateClass = requireClass("com.ikunkk02.crossbowarsenal.arrow.ArrowShotState");
		Constructor<?> stateConstructor = stateClass.getConstructor();
		Object state = stateConstructor.newInstance();
		Method initialize = stateClass.getMethod(
				"initialize", arrowTypeClass, int.class, int.class, double.class,
				boolean.class, boolean.class, boolean.class, double.class, double.class, UUID.class
		);
		Method tryMarkExploded = stateClass.getMethod("tryMarkExploded");
		Method writeNbt = stateClass.getMethod("writeNbt", NbtCompound.class);
		Method readNbt = stateClass.getMethod("readNbt", NbtCompound.class);
		Method getArrowType = stateClass.getMethod("getArrowType");
		Method getExplosionPower = stateClass.getMethod("getExplosionPower");
		Method getShooterUuid = stateClass.getMethod("getShooterUuid");

		UUID shooter = UUID.fromString("12345678-1234-1234-1234-123456789abc");
		initialize.invoke(state, penetrating, 3, 2, 4.0D, true, true, true, 1.25D, 0.35D, shooter);
		assertTrue((boolean) tryMarkExploded.invoke(state), "first explosion claim succeeds");
		assertFalse((boolean) tryMarkExploded.invoke(state), "duplicate explosion claim is rejected");
		NbtCompound nbt = new NbtCompound();
		writeNbt.invoke(state, nbt);
		Object restored = stateConstructor.newInstance();
		readNbt.invoke(restored, nbt);
		assertEquals(penetrating, getArrowType.invoke(restored), "arrow type survives NBT");
		assertDoubleEquals(4.0D, (double) getExplosionPower.invoke(restored), "explosion power survives NBT");
		assertEquals(shooter, getShooterUuid.invoke(restored), "shooter UUID survives NBT");
		assertFalse((boolean) tryMarkExploded.invoke(restored), "exploded flag survives NBT");

		CrossbowArsenalConfig config = new CrossbowArsenalConfig();
		assertBooleanField(config, "enablePenetratingArrow", true);
		assertBooleanField(config, "enableExplosiveArrow", true);
		assertBooleanField(config, "penetratingArrowCanPenetrateFragileBlocks", true);
		assertIntField(config, "penetratingArrowSoftBlockRequiresPiercingLevel", 2);
		assertIntField(config, "penetratingArrowWoodBlockRequiresPiercingLevel", 3);
		assertDoubleField(config, "penetratingArrowDamageMultiplierPerBlock", 0.85D);
		assertDoubleField(config, "penetratingArrowSpeedMultiplierPerBlock", 0.9D);
		assertDoubleField(config, "explosiveArrowBasePower", 2.0D);
		assertDoubleField(config, "explosiveEnchantLevel1Power", 1.5D);
		assertDoubleField(config, "explosiveEnchantLevel2Power", 2.2D);
		assertDoubleField(config, "explosiveEnchantLevel3Power", 3.0D);
		assertDoubleField(config, "maxExplosionPower", 4.0D);
		assertBooleanField(config, "explosiveArrowBreakBlocks", false);
		assertBooleanField(config, "explosiveEnchantBreakBlocks", false);
		assertBooleanField(config, "explosiveArrowFire", false);
		assertDoubleField(config, "explosiveArrowKnockbackMultiplier", 1.0D);
		assertDoubleField(config, "explosiveArrowSelfDamageMultiplier", 0.35D);
		assertBooleanField(config, "explosiveStopsPenetration", true);

		Class<?> projectileInterface = requireClass("com.ikunkk02.crossbowarsenal.arrow.ArrowShotProjectile");
		projectileInterface.getMethod("crossbow_arsenal$getArrowShotState");
		Class<?> shotUtil = requireClass("com.ikunkk02.crossbowarsenal.arrow.SpecialArrowUtil");
		shotUtil.getMethod(
				"applyShotProfile",
				requireClass("net.minecraft.server.world.ServerWorld"),
				requireClass("net.minecraft.entity.LivingEntity"),
				requireClass("net.minecraft.item.ItemStack"),
				requireClass("net.minecraft.entity.projectile.PersistentProjectileEntity")
		);
		Class<?> explosionUtil = requireClass("com.ikunkk02.crossbowarsenal.arrow.ExplosionArrowUtil");
		explosionUtil.getMethod(
				"tryExplode",
				requireClass("net.minecraft.entity.projectile.PersistentProjectileEntity"),
				Vec3d.class
		);
		Class<?> projectileMixin = requireClass("com.ikunkk02.crossbowarsenal.mixin.PersistentProjectileEntityMixin");
		assertTrue(projectileInterface.isAssignableFrom(projectileMixin), "projectile mixin exposes special-arrow state");
		requireClass("com.ikunkk02.crossbowarsenal.mixin.ExplosionMixin");

		Class<?> items = requireClass("com.ikunkk02.crossbowarsenal.item.ModItems");
		items.getField("PENETRATING_ARROW");
		items.getField("EXPLOSIVE_ARROW");
		Class<?> enchantments = requireClass("com.ikunkk02.crossbowarsenal.enchantment.ModEnchantments");
		enchantments.getField("EXPLOSIVE");
		enchantments.getMethod("getExplosiveLevel", requireClass("net.minecraft.world.World"), requireClass("net.minecraft.item.ItemStack"));
	}

	private static Class<?> requireClass(String name) {
		try {
			return Class.forName(name, false, SpecialArrowRulesTest.class.getClassLoader());
		} catch (ClassNotFoundException exception) {
			throw new AssertionError("Missing production class " + name, exception);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Object enumValue(Class<?> enumClass, String name) {
		return Enum.valueOf((Class<? extends Enum>) enumClass.asSubclass(Enum.class), name);
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
		if (Math.abs(expected - actual) > 1.0E-6D) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
