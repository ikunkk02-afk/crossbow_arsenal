package com.ikunkk02.crossbowarsenal.component;

import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.UUID;

public interface LockTargetComponent extends Component {
	@Nullable UUID getLockedTargetUuid();

	int getLockedTargetEntityId();

	long getLastLockTime();

	void setTarget(UUID targetUuid, int targetEntityId, long lockTime);

	void clearTarget();
}
