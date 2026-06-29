package com.ikunkk02.crossbowarsenal.client.network;

import com.ikunkk02.crossbowarsenal.client.lockon.ClientTargetingPolicyState;
import com.ikunkk02.crossbowarsenal.network.LockTargetResultS2CPacket;
import com.ikunkk02.crossbowarsenal.network.TargetingPolicyRequestC2SPacket;
import com.ikunkk02.crossbowarsenal.network.TargetingPolicyS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientTargetingNetworking {
	private ClientTargetingNetworking() {
	}

	public static void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(TargetingPolicyS2CPacket.ID, (payload, context) ->
				context.client().execute(() -> ClientTargetingPolicyState.acceptServerPolicy(payload.toPolicy())));
		ClientPlayNetworking.registerGlobalReceiver(LockTargetResultS2CPacket.ID, (payload, context) ->
				context.client().execute(() -> ClientTargetingPolicyState.acceptLockResult(
						payload.targetEntityId(), payload.accepted(), payload.throughWall(), payload.reason()
				)));
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientTargetingPolicyState.reset());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientTargetingPolicyState.reset());
	}

	public static void requestPolicyRefresh() {
		if (ClientPlayNetworking.canSend(TargetingPolicyRequestC2SPacket.ID)) {
			ClientPlayNetworking.send(TargetingPolicyRequestC2SPacket.INSTANCE);
		}
	}
}
