package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record LockTargetResultS2CPacket(int targetEntityId, boolean accepted, boolean throughWall, String reason) implements CustomPayload {
	public static final Id<LockTargetResultS2CPacket> ID = new Id<>(Crossbow_arsenal.id("lock_target_result"));
	public static final PacketCodec<RegistryByteBuf, LockTargetResultS2CPacket> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, LockTargetResultS2CPacket::targetEntityId,
			PacketCodecs.BOOL, LockTargetResultS2CPacket::accepted,
			PacketCodecs.BOOL, LockTargetResultS2CPacket::throughWall,
			PacketCodecs.string(128), LockTargetResultS2CPacket::reason,
			LockTargetResultS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
