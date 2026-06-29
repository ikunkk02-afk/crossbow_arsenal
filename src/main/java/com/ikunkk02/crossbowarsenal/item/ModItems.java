package com.ikunkk02.crossbowarsenal.item;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	public static final Item LOCK_ON_SIGHT = register("lock_on_sight", new Item(new Item.Settings().maxCount(16)));
	public static final Item PENETRATING_ARROW = register(
			"penetrating_arrow",
			new SpecialArrowItem(new Item.Settings(), "tooltip.crossbow_arsenal.penetrating_arrow")
	);
	public static final Item EXPLOSIVE_ARROW = register(
			"explosive_arrow",
			new SpecialArrowItem(new Item.Settings(), "tooltip.crossbow_arsenal.explosive_arrow")
	);

	private ModItems() {
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(LOCK_ON_SIGHT);
			entries.add(PENETRATING_ARROW);
			entries.add(EXPLOSIVE_ARROW);
		});
	}

	private static Item register(String path, Item item) {
		return Registry.register(Registries.ITEM, Crossbow_arsenal.id(path), item);
	}
}
