package com.ikunkk02.crossbowarsenal.recipe;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModRecipes {
	public static final RecipeSerializer<LockOnSightAttachmentRecipe> LOCK_ON_SIGHT_ATTACHMENT = Registry.register(
			Registries.RECIPE_SERIALIZER,
			Crossbow_arsenal.id("lock_on_sight_attachment"),
			new SpecialRecipeSerializer<>(LockOnSightAttachmentRecipe::new)
	);

	private ModRecipes() {
	}

	public static void initialize() {
	}
}
