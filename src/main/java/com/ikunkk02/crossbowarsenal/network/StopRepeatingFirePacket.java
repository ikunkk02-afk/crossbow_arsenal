package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StopRepeatingFirePacket() implements CustomPayload {
	public static final StopRepeatingFirePacket INSTANCE = new StopRepeatingFirePacket();
	public static final Id<StopRepeatingFirePacket> ID = new Id<>(Crossbow_arsenal.id("stop_repeating_fire"));
	public static final PacketCodec<RegistryByteBuf, StopRepeatingFirePacket> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
