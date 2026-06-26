package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StartRepeatingFirePacket() implements CustomPayload {
	public static final StartRepeatingFirePacket INSTANCE = new StartRepeatingFirePacket();
	public static final Id<StartRepeatingFirePacket> ID = new Id<>(Crossbow_arsenal.id("start_repeating_fire"));
	public static final PacketCodec<RegistryByteBuf, StartRepeatingFirePacket> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
