package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.config.CrossbowArsenalConfigManager;
import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;
import com.ikunkk02.crossbowarsenal.lockon.LockOnManager;
import com.ikunkk02.crossbowarsenal.repeating.RepeatingCrossbowManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.playC2S().register(StartRepeatingFirePacket.ID, StartRepeatingFirePacket.CODEC);
		PayloadTypeRegistry.playC2S().register(StopRepeatingFirePacket.ID, StopRepeatingFirePacket.CODEC);
		PayloadTypeRegistry.playC2S().register(LockTargetPacket.ID, LockTargetPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(TargetingPolicyRequestC2SPacket.ID, TargetingPolicyRequestC2SPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(TargetingPolicyS2CPacket.ID, TargetingPolicyS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(LockTargetResultS2CPacket.ID, LockTargetResultS2CPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(StartRepeatingFirePacket.ID, (payload, context) ->
				context.server().execute(() -> RepeatingCrossbowManager.handleStart(context.player())));
		ServerPlayNetworking.registerGlobalReceiver(StopRepeatingFirePacket.ID, (payload, context) ->
				context.server().execute(() -> RepeatingCrossbowManager.handleStop(context.player())));
		ServerPlayNetworking.registerGlobalReceiver(LockTargetPacket.ID, (payload, context) ->
				context.server().execute(() -> LockOnManager.handleTargetPacket(context.player(), payload.targetEntityId())));
		ServerPlayNetworking.registerGlobalReceiver(TargetingPolicyRequestC2SPacket.ID, (payload, context) ->
				context.server().execute(() -> sendTargetingPolicy(context.player())));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendTargetingPolicy(handler.player));
	}

	public static void sendTargetingPolicy(ServerPlayerEntity player) {
		OverpoweredTargetingPolicy policy = OverpoweredTargetingPolicy.fromConfig(CrossbowArsenalConfigManager.getConfig());
		ServerPlayNetworking.send(player, TargetingPolicyS2CPacket.fromPolicy(policy));
	}
}
