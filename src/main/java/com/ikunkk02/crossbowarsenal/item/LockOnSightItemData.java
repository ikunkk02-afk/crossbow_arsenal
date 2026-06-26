package com.ikunkk02.crossbowarsenal.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class LockOnSightItemData {
	private static final String HAS_LOCK_ON_SIGHT = "has_lock_on_sight";

	private LockOnSightItemData() {
	}

	public static boolean hasLockOnSight(ItemStack stack) {
		return stack.isOf(Items.CROSSBOW)
				&& stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt().getBoolean(HAS_LOCK_ON_SIGHT);
	}

	public static void setLockOnSight(ItemStack stack) {
		NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> nbt.putBoolean(HAS_LOCK_ON_SIGHT, true));
	}
}
