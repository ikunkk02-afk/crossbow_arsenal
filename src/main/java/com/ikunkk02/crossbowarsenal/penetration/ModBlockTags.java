package com.ikunkk02.crossbowarsenal.penetration;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModBlockTags {
	public static final TagKey<Block> ARROW_BREAKABLE_GLASS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("arrow_breakable_glass"));
	public static final TagKey<Block> ARROW_FRAGILE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Crossbow_arsenal.id("arrow_fragile_blocks"));

	private ModBlockTags() {
	}
}
