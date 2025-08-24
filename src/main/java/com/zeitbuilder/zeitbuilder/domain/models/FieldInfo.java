package com.zeitbuilder.zeitbuilder.domain.models;

public class FieldInfo {
	private final String name;
	private final boolean defaultSelected;

	public FieldInfo(String name, boolean defaultSelected) {
		this.name = name;
		this.defaultSelected = defaultSelected;
	}

	public String getName() { return name; }
	public boolean isDefaultSelected() { return defaultSelected; }
}
