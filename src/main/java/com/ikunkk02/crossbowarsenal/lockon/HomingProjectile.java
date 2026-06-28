package com.ikunkk02.crossbowarsenal.lockon;

import java.util.UUID;

public interface HomingProjectile {
	void crossbow_arsenal$setHomingTarget(UUID targetUuid, int homingTicks, double strength, double maxDistance, double originalSpeed);

	boolean crossbow_arsenal$isHoming();

	void crossbow_arsenal$weakenHoming(double multiplier);
}
