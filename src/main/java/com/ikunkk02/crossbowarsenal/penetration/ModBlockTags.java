package com.ikunkk02.crossbowarsenal.penetration;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModBlockTags {
	public static final TagKey<Block> ARROW_BREAKABLE_GLASS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("arrow_breakable_glass"));
	public static final TagKey<Block> ARROW_FRAGILE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("arrow_fragile_blocks"));
	public static final TagKey<Block> PENETRATING_ARROW_FRAGILE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("penetrating_arrow_fragile_blocks"));
	public static final TagKey<Block> PENETRATING_ARROW_SOFT_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("penetrating_arrow_soft_blocks"));
	public static final TagKey<Block> PENETRATING_ARROW_WOODEN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("penetrating_arrow_wooden_blocks"));
	public static final TagKey<Block> PENETRATING_ARROW_STONE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("penetrating_arrow_stone_blocks"));
	public static final TagKey<Block> PENETRATING_ARROW_NEVER_PENETRATE = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("penetrating_arrow_never_penetrate"));
	public static final TagKey<Block> OVERPOWERED_PENETRABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("overpowered_penetrable_blocks"));
	public static final TagKey<Block> OVERPOWERED_NEVER_BREAK_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("overpowered_never_break_blocks"));

	private ModBlockTags() {
	}
}
