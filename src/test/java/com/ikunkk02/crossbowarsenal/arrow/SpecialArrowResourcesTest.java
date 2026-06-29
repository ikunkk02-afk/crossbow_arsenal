package com.ikunkk02.crossbowarsenal.arrow;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class SpecialArrowResourcesTest {
	private static final Path RESOURCES = Path.of("src", "main", "resources");

	private SpecialArrowResourcesTest() {
	}

	public static void main(String[] args) throws IOException {
		for (String item : new String[]{"penetrating_arrow", "explosive_arrow"}) {
			assertRegularFile(RESOURCES.resolve("assets/crossbow_arsenal/models/item/" + item + ".json"));
			assertRegularFile(RESOURCES.resolve("assets/crossbow_arsenal/textures/item/" + item + ".png"));
			String recipe = readRequired(RESOURCES.resolve("data/crossbow_arsenal/recipe/" + item + ".json"));
			assertContains(recipe, "\"category\": \"equipment\"", item + " recipe category");
			assertEquals(4, countOccurrences(recipe, "\"item\": \"minecraft:arrow\""), item + " recipe arrow count");
		}

		String arrowTag = readRequired(RESOURCES.resolve("data/minecraft/tags/item/arrows.json"));
		assertContains(arrowTag, "crossbow_arsenal:penetrating_arrow", "arrow item tag");
		assertContains(arrowTag, "crossbow_arsenal:explosive_arrow", "arrow item tag");

		Path blockTags = RESOURCES.resolve("data/crossbow_arsenal/tags/block");
		String fragile = readRequired(blockTags.resolve("penetrating_arrow_fragile_blocks.json"));
		String soft = readRequired(blockTags.resolve("penetrating_arrow_soft_blocks.json"));
		String wooden = readRequired(blockTags.resolve("penetrating_arrow_wooden_blocks.json"));
		String never = readRequired(blockTags.resolve("penetrating_arrow_never_penetrate.json"));
		for (String required : new String[]{"#crossbow_arsenal:arrow_breakable_glass", "#minecraft:leaves", "minecraft:cobweb", "minecraft:bamboo", "minecraft:short_grass", "minecraft:vine"}) {
			assertContains(fragile, required, "fragile special-arrow tag");
		}
		for (String required : new String[]{"#minecraft:dirt", "minecraft:sand", "minecraft:gravel", "minecraft:clay", "minecraft:snow_block"}) {
			assertContains(soft, required, "soft special-arrow tag");
		}
		for (String required : new String[]{"#minecraft:logs", "#minecraft:planks", "#minecraft:wooden_fences", "#minecraft:wooden_doors"}) {
			assertContains(wooden, required, "wooden special-arrow tag");
		}
		for (String required : new String[]{"#minecraft:base_stone_overworld", "#minecraft:coal_ores", "#minecraft:diamond_ores", "minecraft:iron_block", "minecraft:obsidian", "minecraft:bedrock", "minecraft:chest", "minecraft:crafting_table", "minecraft:furnace", "minecraft:spawner"}) {
			assertContains(never, required, "never-penetrate tag");
		}

		String repeating = readRequired(RESOURCES.resolve("data/crossbow_arsenal/enchantment/repeating.json"));
		assertContains(repeating, "#crossbow_arsenal:exclusive_set/repeating", "Repeating exclusive set");
		assertContains(repeating, "\"supported_items\": \"#minecraft:enchantable/crossbow\"", "Repeating supported items");
		assertContains(repeating, "\"weight\": 5", "Repeating enchanting-table weight");
		String repeatingExclusive = readRequired(RESOURCES.resolve("data/crossbow_arsenal/tags/enchantment/exclusive_set/repeating.json"));
		assertContains(repeatingExclusive, "minecraft:multishot", "Repeating exclusive set");
		assertNotContains(repeatingExclusive, "minecraft:piercing", "Repeating exclusive set");
		assertMissing(RESOURCES.resolve("data/minecraft/tags/enchantment/exclusive_set/crossbow.json"));

		String explosiveEnchant = readRequired(RESOURCES.resolve("data/crossbow_arsenal/enchantment/explosive.json"));
		assertContains(explosiveEnchant, "enchantment.crossbow_arsenal.explosive", "Explosive enchantment");
		assertContains(explosiveEnchant, "\"max_level\": 3", "Explosive enchantment");
		assertContains(explosiveEnchant, "\"supported_items\": \"#minecraft:enchantable/crossbow\"", "Explosive supported items");
		assertContains(explosiveEnchant, "\"weight\": 2", "Explosive enchanting-table weight");
		assertNotContains(explosiveEnchant, "exclusive_set", "Explosive enchantment");
		for (String tag : new String[]{"in_enchanting_table", "non_treasure", "on_random_loot", "tooltip_order", "tradeable"}) {
			String content = readRequired(RESOURCES.resolve("data/minecraft/tags/enchantment/" + tag + ".json"));
			assertContains(content, "crossbow_arsenal:repeating", tag + " enchantment tag");
			assertContains(content, "crossbow_arsenal:explosive", tag + " enchantment tag");
		}

		String english = readRequired(RESOURCES.resolve("assets/crossbow_arsenal/lang/en_us.json"));
		String chinese = readRequired(RESOURCES.resolve("assets/crossbow_arsenal/lang/zh_cn.json"));
		for (String key : new String[]{
				"item.crossbow_arsenal.penetrating_arrow", "item.crossbow_arsenal.explosive_arrow",
				"enchantment.crossbow_arsenal.explosive", "tooltip.crossbow_arsenal.penetrating_arrow",
				"tooltip.crossbow_arsenal.explosive_arrow", "tooltip.crossbow_arsenal.explosive",
				"config.crossbow_arsenal.enablePenetratingArrow", "config.crossbow_arsenal.enableExplosiveArrow",
				"config.crossbow_arsenal.explosiveArrowSelfDamageMultiplier", "config.crossbow_arsenal.explosiveArrowKnockbackMultiplier"
		}) {
			assertContains(english, key, "English language file");
			assertContains(chinese, key, "Chinese language file");
		}

		String repeatingManager = readRequired(Path.of("src/main/java/com/ikunkk02/crossbowarsenal/repeating/RepeatingCrossbowManager.java"));
		assertNotContains(repeatingManager, "hasAny(world, crossbow, Enchantments.PIERCING)", "Repeating runtime compatibility");

		String sightModel = readRequired(RESOURCES.resolve("assets/crossbow_arsenal/models/item/lock_on_sight.json"));
		assertContains(sightModel, "crossbow_arsenal:item/lock_on_sight", "lock-on sight model texture");
		assertNotContains(sightModel, "minecraft:item/spyglass", "lock-on sight model texture");
		Path sightTexture = RESOURCES.resolve("assets/crossbow_arsenal/textures/item/lock_on_sight.png");
		assertRegularFile(sightTexture);
		BufferedImage image = ImageIO.read(sightTexture.toFile());
		if (image == null || image.getWidth() != 16 || image.getHeight() != 16) {
			throw new AssertionError("Lock-on sight texture must be a readable 16x16 PNG");
		}
	}

	private static String readRequired(Path path) throws IOException {
		assertRegularFile(path);
		return Files.readString(path);
	}

	private static void assertRegularFile(Path path) {
		if (!Files.isRegularFile(path)) {
			throw new AssertionError("Missing resource " + path);
		}
	}

	private static void assertMissing(Path path) {
		if (Files.exists(path)) {
			throw new AssertionError("Resource must be removed " + path);
		}
	}

	private static void assertContains(String text, String value, String message) {
		if (!text.contains(value)) {
			throw new AssertionError(message + " must contain " + value);
		}
	}

	private static void assertNotContains(String text, String value, String message) {
		if (text.contains(value)) {
			throw new AssertionError(message + " must not contain " + value);
		}
	}

	private static int countOccurrences(String text, String value) {
		int count = 0;
		int index = 0;
		while ((index = text.indexOf(value, index)) >= 0) {
			count++;
			index += value.length();
		}
		return count;
	}

	private static void assertEquals(int expected, int actual, String message) {
		if (expected != actual) {
			throw new AssertionError(message + ": expected " + expected + " but got " + actual);
		}
	}
}
