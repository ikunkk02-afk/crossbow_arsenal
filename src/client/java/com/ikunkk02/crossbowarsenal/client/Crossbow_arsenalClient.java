package com.ikunkk02.crossbowarsenal.client;

import com.ikunkk02.crossbowarsenal.client.hud.LockOnHudRenderer;
import com.ikunkk02.crossbowarsenal.client.hud.OverpoweredWarningController;
import com.ikunkk02.crossbowarsenal.client.hud.OverpoweredWarningRenderer;
import com.ikunkk02.crossbowarsenal.client.hud.StartupHudRenderer;
import com.ikunkk02.crossbowarsenal.client.input.RepeatingCrossbowInputHandler;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnClientEvents;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnScreenProjection;
import com.ikunkk02.crossbowarsenal.client.lockon.LockOnStartupHudController;
import com.ikunkk02.crossbowarsenal.client.network.ClientTargetingNetworking;
import net.fabricmc.api.ClientModInitializer;

public class Crossbow_arsenalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTargetingNetworking.initialize();
		LockOnScreenProjection.initialize();
		RepeatingCrossbowInputHandler.initialize();
		LockOnClientEvents.initialize();
		LockOnStartupHudController.initialize();
		StartupHudRenderer.initialize();
		LockOnHudRenderer.initialize();
		OverpoweredWarningController.initialize();
		OverpoweredWarningRenderer.initialize();
	}
}
