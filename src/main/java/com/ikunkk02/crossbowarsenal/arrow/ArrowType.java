package com.ikunkk02.crossbowarsenal.arrow;

public enum ArrowType {
	NORMAL("normal"),
	PENETRATING("penetrating"),
	EXPLOSIVE("explosive");

	private final String id;

	ArrowType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static ArrowType fromId(String id) {
		for (ArrowType type : values()) {
			if (type.id.equals(id)) {
				return type;
			}
		}
		return NORMAL;
	}
}
