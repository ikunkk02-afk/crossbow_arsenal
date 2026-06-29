package com.ikunkk02.crossbowarsenal.item;

import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class SpecialArrowItem extends ArrowItem {
	private final String tooltipKey;

	public SpecialArrowItem(Item.Settings settings, String tooltipKey) {
		super(settings);
		this.tooltipKey = tooltipKey;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		tooltip.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
	}
}
