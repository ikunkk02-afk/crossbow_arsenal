package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record LockTargetPacket(int targetEntityId) implements CustomPayload {
	public static final CustomPayload.Id<LockTargetPacket> ID = new CustomPayload.Id<>(Crossbow_arsenal.id("lock_target"));
	public static final PacketCodec<RegistryByteBuf, LockTargetPacket> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT,
			LockTargetPacket::targetEntityId,
			LockTargetPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
