package com.ikunkk02.crossbowarsenal.damage;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModDamageTypes {
	public static final RegistryKey<DamageType> REPEATING_ARROW = RegistryKey.of(
			RegistryKeys.DAMAGE_TYPE,
			Identifier.of(Crossbow_arsenal.MOD_ID, "repeating_arrow")
	);

	private ModDamageTypes() {
	}
}
