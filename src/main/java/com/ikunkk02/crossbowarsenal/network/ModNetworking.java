package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.lockon.LockOnManager;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingCrossbowManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.playC2S().register(StartRepeatingFirePacket.ID, StartRepeatingFirePacket.CODEC);
		PayloadTypeRegistry.playC2S().register(StopRepeatingFirePacket.ID, StopRepeatingFirePacket.CODEC);
		PayloadTypeRegistry.playC2S().register(LockTargetPacket.ID, LockTargetPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(StartRepeatingFirePacket.ID, (payload, context) ->
				context.server().execute(() -> RepeatingCrossbowManager.handleStart(context.player())));
		ServerPlayNetworking.registerGlobalReceiver(StopRepeatingFirePacket.ID, (payload, context) ->
				context.server().execute(() -> RepeatingCrossbowManager.handleStop(context.player())));
		ServerPlayNetworking.registerGlobalReceiver(LockTargetPacket.ID, (payload, context) ->
				context.server().execute(() -> LockOnManager.handleTargetPacket(context.player(), payload.targetEntityId())));
	}
}
