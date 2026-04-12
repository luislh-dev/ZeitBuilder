package com.zeitbuilder.zeitbuilder.model;

public enum HierarchyType {
	STANDARD("Standard"),
	EXTENSIBLE("Extensible");

	private final String displayName;

	HierarchyType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
