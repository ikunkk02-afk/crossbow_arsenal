package com.ikunkk02.crossbowarsenal.component;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public final class ModComponents implements EntityComponentInitializer {
	public static final ComponentKey<LockTargetComponent> LOCK_TARGET = ComponentRegistry.getOrCreate(
			Crossbow_arsenal.id("lock_target"),
			LockTargetComponent.class
	);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(PlayerEntity.class, LOCK_TARGET)
				.impl(PlayerLockTargetComponent.class)
				.respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
				.end(PlayerLockTargetComponent::new);
	}
}
