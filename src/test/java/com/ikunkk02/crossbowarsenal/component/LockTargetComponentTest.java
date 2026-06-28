package com.ikunkk02.crossbowarsenal.component;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public final class LockTargetComponentTest {
	private LockTargetComponentTest() {
	}

	public static void main(String[] args) {
		UUID targetUuid = UUID.fromString("12345678-1234-5678-9abc-def012345678");
		PlayerLockTargetComponent component = new PlayerLockTargetComponent(null);
		component.setTarget(targetUuid, 42, 1234L);

		assertEquals(targetUuid, component.getLockedTargetUuid(), "target UUID after set");
		assertEquals(42, component.getLockedTargetEntityId(), "target entity id after set");
		assertEquals(1234L, component.getLastLockTime(), "last lock time after set");

		NbtCompound nbt = new NbtCompound();
		component.writeToNbt(nbt, null);
		PlayerLockTargetComponent restored = new PlayerLockTargetComponent(null);
		restored.readFromNbt(nbt, null);

		assertEquals(targetUuid, restored.getLockedTargetUuid(), "target UUID after NBT round trip");
		assertEquals(42, restored.getLockedTargetEntityId(), "target entity id after NBT round trip");
		assertEquals(1234L, restored.getLastLockTime(), "last lock time after NBT round trip");

		restored.clearTarget();
		assertEquals(null, restored.getLockedTargetUuid(), "target UUID after clear");
		assertEquals(-1, restored.getLockedTargetEntityId(), "target entity id after clear");
		assertEquals(0L, restored.getLastLockTime(), "last lock time after clear");
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!java.util.Objects.equals(expected, actual)) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}

	private static void assertEquals(long expected, long actual, String message) {
		if (expected != actual) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
