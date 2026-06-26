package com.ikunkk02.crossbowarsenal.client.input;

import com.ikunkk02.crossbowarsenal.network.StartRepeatingFirePacket;
import com.ikunkk02.crossbowarsenal.network.StopRepeatingFirePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public final class RepeatingCrossbowInputHandler {
	private static boolean wasUseKeyPressed;

	private RepeatingCrossbowInputHandler() {
	}

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(RepeatingCrossbowInputHandler::tick);
	}

	private static void tick(MinecraftClient client) {
		if (client.player == null || client.world == null) {
			wasUseKeyPressed = false;
			return;
		}

		boolean pressed = client.options.useKey.isPressed();
		if (pressed && !wasUseKeyPressed) {
			ClientPlayNetworking.send(StartRepeatingFirePacket.INSTANCE);
		} else if (!pressed && wasUseKeyPressed) {
			ClientPlayNetworking.send(StopRepeatingFirePacket.INSTANCE);
		}
		wasUseKeyPressed = pressed;
	}
}
