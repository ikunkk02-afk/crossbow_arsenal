package com.ikunkk02.crossbowarsenal.network;

import com.ikunkk02.crossbowarsenal.Crossbow_arsenal;
import com.ikunkk02.crossbowarsenal.config.OverpoweredTargetingPolicy;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record TargetingPolicyS2CPacket(
		boolean enabled,
		boolean allowTargetPlayers,
		boolean allowTargetThroughWalls,
		boolean allowHomingThroughWalls,
		boolean allowTargetInvisibleEntities,
		double overpoweredTargetMaxDistance
) implements CustomPayload {
	public static final Id<TargetingPolicyS2CPacket> ID = new Id<>(Crossbow_arsenal.id("targeting_policy"));
	public static final PacketCodec<RegistryByteBuf, TargetingPolicyS2CPacket> CODEC = PacketCodec.tuple(
			PacketCodecs.BOOL, TargetingPolicyS2CPacket::enabled,
			PacketCodecs.BOOL, TargetingPolicyS2CPacket::allowTargetPlayers,
			PacketCodecs.BOOL, TargetingPolicyS2CPacket::allowTargetThroughWalls,
			PacketCodecs.BOOL, TargetingPolicyS2CPacket::allowHomingThroughWalls,
			PacketCodecs.BOOL, TargetingPolicyS2CPacket::allowTargetInvisibleEntities,
			PacketCodecs.DOUBLE, TargetingPolicyS2CPacket::overpoweredTargetMaxDistance,
			TargetingPolicyS2CPacket::new
	);

	public static TargetingPolicyS2CPacket fromPolicy(OverpoweredTargetingPolicy policy) {
		return new TargetingPolicyS2CPacket(
				policy.enabled(),
				policy.allowTargetPlayers(),
				policy.allowTargetThroughWalls(),
				policy.allowHomingThroughWalls(),
				policy.allowTargetInvisibleEntities(),
				policy.overpoweredTargetMaxDistance()
		);
	}

	public OverpoweredTargetingPolicy toPolicy() {
		return new OverpoweredTargetingPolicy(
				enabled,
				allowTargetPlayers,
				allowTargetThroughWalls,
				allowHomingThroughWalls,
				allowTargetInvisibleEntities,
				overpoweredTargetMaxDistance
		);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
