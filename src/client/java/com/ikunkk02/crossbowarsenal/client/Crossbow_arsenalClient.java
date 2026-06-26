package com.ikunkk02.crossbowarsenal.client;

import com.ikunkk02.crossbowarsenal.client.hud.LockOnHudRenderer;
import com.ikunkk02.crossbowarsenal.client.input.RepeatingCrossbowInputHandler;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnClientEvents;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnScreenProjection;
import net.fabricmc.api.ClientModInitializer;

public class Crossbow_arsenalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LockOnScreenProjection.initialize();
		RepeatingCrossbowInputHandler.initialize();
		LockOnClientEvents.initialize();
		LockOnHudRenderer.initialize();
	}
}
