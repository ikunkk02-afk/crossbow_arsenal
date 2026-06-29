package com.ikunkk02.crossbowarsenal.enchantment;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.Optional;

public final class ModEnchantments {
	public static final RegistryKey<Enchantment> REPEATING = RegistryKey.of(
			RegistryKeys.ENCHANTMENT,
			Crossbow_arsenal.id("repeating")
	);
	public static final RegistryKey<Enchantment> EXPLOSIVE = RegistryKey.of(
			RegistryKeys.ENCHANTMENT,
			Crossbow_arsenal.id("explosive")
	);

	private ModEnchantments() {
	}

	public static void initialize() {
		Crossbow_arsenal.LOGGER.debug("Registered Crossbow Arsenal enchantment keys");
	}

	public static int getRepeatingLevel(World world, ItemStack stack) {
		return getLevel(world, stack, REPEATING);
	}

	public static int getExplosiveLevel(World world, ItemStack stack) {
		return getLevel(world, stack, EXPLOSIVE);
	}

	public static int getLevel(World world, ItemStack stack, RegistryKey<Enchantment> key) {
		Optional<Registry<Enchantment>> registry = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
		return registry.flatMap(enchantments -> enchantments.getEntry(key))
				.map(entry -> EnchantmentHelper.getLevel(entry, stack))
				.orElse(0);
	}

	public static boolean hasAny(World world, ItemStack stack, RegistryKey<Enchantment> key) {
		return getLevel(world, stack, key) > 0;
	}
}
