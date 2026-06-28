package com.ikunkk02.crossbowarsenal.penetration;

public interface PenetratingProjectile {
	void crossbow_arsenal$initializePenetration(boolean canGlass, boolean canFragile, boolean glassConsumesDurability, int maxFragileBlocks, int maxEntityPenetrations);

	PenetrationState crossbow_arsenal$getPenetrationState();
}
