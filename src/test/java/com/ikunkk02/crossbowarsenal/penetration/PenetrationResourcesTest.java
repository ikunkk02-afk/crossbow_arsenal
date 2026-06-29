package com.ikunkk02.crossbowarsenal.penetration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PenetrationResourcesTest {
	private static final Path BLOCK_TAGS = Path.of("src", "main", "resources", "data", "crossbow_arsenal", "tags", "block");

	private PenetrationResourcesTest() {
	}

	public static void main(String[] args) throws IOException {
		String glass = readRequired(BLOCK_TAGS.resolve("arrow_breakable_glass.json"));
		for (String required : new String[]{
			"minecraft:glass", "minecraft:glass_pane", "minecraft:white_stained_glass",
			"minecraft:black_stained_glass_pane", "minecraft:tinted_glass"
		}) {
			assertContains(glass, required, "glass tag");
		}

		String fragile = readRequired(BLOCK_TAGS.resolve("arrow_fragile_blocks.json"));
		for (String required : new String[]{
			"#minecraft:leaves", "#minecraft:crops", "#minecraft:saplings", "#minecraft:flowers",
			"minecraft:cobweb", "minecraft:bamboo", "minecraft:vine", "minecraft:mangrove_roots",
			"minecraft:muddy_mangrove_roots", "minecraft:sugar_cane", "minecraft:cactus",
			"minecraft:kelp", "minecraft:seagrass", "minecraft:moss_block",
			"minecraft:red_mushroom_block", "minecraft:crimson_fungus", "minecraft:nether_wart",
			"minecraft:melon", "minecraft:pumpkin"
		}) {
			assertContains(fragile, required, "fragile tag");
		}
		for (String forbidden : new String[]{
			"minecraft:stone", "minecraft:dirt", "minecraft:oak_log", "minecraft:chest",
			"minecraft:crafting_table", "minecraft:furnace", "minecraft:redstone_block",
			"minecraft:iron_block", "minecraft:obsidian", "minecraft:bedrock"
		}) {
			assertNotContains(fragile, forbidden, "fragile tag");
		}

		String wooden = readRequired(BLOCK_TAGS.resolve("penetrating_arrow_wooden_blocks.json"));
		for (String required : new String[]{
			"#minecraft:logs", "#minecraft:planks", "#minecraft:wooden_fences",
			"#minecraft:wooden_doors", "#minecraft:wooden_trapdoors"
		}) {
			assertContains(wooden, required, "wooden penetration tag");
		}

		String stone = readRequired(BLOCK_TAGS.resolve("penetrating_arrow_stone_blocks.json"));
		for (String required : new String[]{
			"#minecraft:base_stone_overworld", "minecraft:cobblestone", "minecraft:stone_bricks",
			"minecraft:cobbled_deepslate", "minecraft:deepslate_bricks", "minecraft:deepslate_tiles"
		}) {
			assertContains(stone, required, "stone penetration tag");
		}
		for (String forbidden : new String[]{"minecraft:obsidian", "minecraft:diamond_ore", "minecraft:iron_ore"}) {
			assertNotContains(stone, forbidden, "stone penetration tag");
		}

		String overpowered = readRequired(BLOCK_TAGS.resolve("overpowered_penetrable_blocks.json"));
		for (String required : new String[]{
			"#crossbow_arsenal:penetrating_arrow_fragile_blocks",
			"#crossbow_arsenal:penetrating_arrow_soft_blocks",
			"#crossbow_arsenal:penetrating_arrow_wooden_blocks",
			"#crossbow_arsenal:penetrating_arrow_stone_blocks"
		}) {
			assertContains(overpowered, required, "overpowered penetrable tag");
		}

		String neverBreak = readRequired(BLOCK_TAGS.resolve("overpowered_never_break_blocks.json"));
		for (String required : new String[]{
			"minecraft:bedrock", "minecraft:obsidian", "minecraft:reinforced_deepslate",
			"minecraft:barrier", "minecraft:command_block", "minecraft:structure_block",
			"minecraft:spawner", "minecraft:chest", "minecraft:barrel", "minecraft:furnace",
			"minecraft:crafting_table", "#minecraft:shulker_boxes"
		}) {
			assertContains(neverBreak, required, "overpowered never-break tag");
		}

		String english = readRequired(Path.of("src", "main", "resources", "assets", "crossbow_arsenal", "lang", "en_us.json"));
		String chinese = readRequired(Path.of("src", "main", "resources", "assets", "crossbow_arsenal", "lang", "zh_cn.json"));
		for (String requiredKey : new String[]{
			"config.crossbow_arsenal.category.penetration",
			"config.crossbow_arsenal.enableGlassPenetration",
			"config.crossbow_arsenal.glassPenetrationDamageMultiplier",
			"config.crossbow_arsenal.glassPenetrationSpeedMultiplier",
			"config.crossbow_arsenal.glassPenetrationConsumesDurability",
			"config.crossbow_arsenal.fragileBlockPenetrationEnabled",
			"config.crossbow_arsenal.fragileBlockDamageMultiplier",
			"config.crossbow_arsenal.fragileBlockSpeedMultiplier",
			"config.crossbow_arsenal.maxFragileBlocksPenetrated",
			"config.crossbow_arsenal.entityPenetrationEnabled",
			"config.crossbow_arsenal.entityPenetrationDamageDecay",
			"config.crossbow_arsenal.maxEntityPenetrations",
			"config.crossbow_arsenal.lockOnArrowCanPenetrateGlass",
			"config.crossbow_arsenal.lockOnArrowCanPenetrateFragileBlocks",
			"config.crossbow_arsenal.showPenetrationDebug",
			"config.crossbow_arsenal.overpoweredPenetrationBreaksStone",
			"config.crossbow_arsenal.overpoweredPenetrationBreaksWood",
			"config.crossbow_arsenal.maxOverpoweredBlocksPenetrated",
			"config.crossbow_arsenal.overpoweredHardBlockSpeedMultiplier",
			"config.crossbow_arsenal.overpoweredHardBlockDamageMultiplier"
		}) {
			assertContains(english, requiredKey, "English language file");
			assertContains(chinese, requiredKey, "Chinese language file");
		}

		String projectileMixin = readRequired(Path.of("src", "main", "java", "com", "ikunkk02", "crossbowarsenal", "mixin", "PersistentProjectileEntityMixin.java"));
		assertNotContains(projectileMixin, "setNoClip(true)", "penetration collision path");
		assertNotContains(projectileMixin, "shouldPhaseThroughBlockingCollision", "penetration collision path");
	}

	private static String readRequired(Path path) throws IOException {
		if (!Files.isRegularFile(path)) {
			throw new AssertionError("Missing resource " + path);
		}
		return Files.readString(path);
	}

	private static void assertContains(String text, String value, String message) {
		if (!text.contains('"' + value + '"')) {
			throw new AssertionError(message + " must contain " + value);
		}
	}

	private static void assertNotContains(String text, String value, String message) {
		if (text.contains('"' + value + '"')) {
			throw new AssertionError(message + " must not contain " + value);
		}
	}
}
