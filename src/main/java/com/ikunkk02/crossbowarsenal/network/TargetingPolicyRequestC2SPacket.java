package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record TargetingPolicyRequestC2SPacket() implements CustomPayload {
	public static final TargetingPolicyRequestC2SPacket INSTANCE = new TargetingPolicyRequestC2SPacket();
	public static final Id<TargetingPolicyRequestC2SPacket> ID = new Id<>(Crossbow_arsenal.id("targeting_policy_request"));
	public static final PacketCodec<RegistryByteBuf, TargetingPolicyRequestC2SPacket> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
