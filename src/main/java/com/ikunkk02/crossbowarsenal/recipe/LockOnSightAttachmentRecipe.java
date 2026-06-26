package com.ikunkk02.crossbowarsenal.recipe;

import com.ikunkk02.crossbowarsenal.item.LockOnSightItemData;
import com.ikunkk02.crossbowarsenal.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class LockOnSightAttachmentRecipe extends SpecialCraftingRecipe {
	public LockOnSightAttachmentRecipe(CraftingRecipeCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingRecipeInput input, World world) {
		return findResult(input) != null;
	}

	@Override
	public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
		ItemStack crossbow = findResult(input);
		if (crossbow == null) {
			return ItemStack.EMPTY;
		}

		ItemStack result = crossbow.copyWithCount(1);
		LockOnSightItemData.setLockOnSight(result);
		return result;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.LOCK_ON_SIGHT_ATTACHMENT;
	}

	private static ItemStack findResult(CraftingRecipeInput input) {
		ItemStack crossbow = ItemStack.EMPTY;
		boolean foundSight = false;

		for (int slot = 0; slot < input.getSize(); slot++) {
			ItemStack stack = input.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}

			if (stack.isOf(Items.CROSSBOW) && !LockOnSightItemData.hasLockOnSight(stack) && crossbow.isEmpty()) {
				crossbow = stack;
			} else if (stack.isOf(ModItems.LOCK_ON_SIGHT) && !foundSight) {
				foundSight = true;
			} else {
				return null;
			}
		}

		return !crossbow.isEmpty() && foundSight ? crossbow : null;
	}
}
