package com.zeitbuilder.zeitbuilder.model;

import java.util.List;

public class BuilderSelection {
	private final List<String> fieldNames;
	private final boolean includeInBuilder;
	private final boolean cancelled;
	private final HierarchyType hierarchyType;

	public BuilderSelection(List<String> fieldNames, boolean includeInBuilder, boolean cancelled, HierarchyType hierarchyType) {
		this.fieldNames = fieldNames;
		this.includeInBuilder = includeInBuilder;
		this.cancelled = cancelled;
		this.hierarchyType = hierarchyType;
	}

	public static BuilderSelection empty() {
		return new BuilderSelection(List.of(), false, false, HierarchyType.STANDARD);
	}

	public static BuilderSelection cancelled() {
		return new BuilderSelection(List.of(), false, true, HierarchyType.STANDARD);
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public boolean isIncludeInBuilder() {
		return includeInBuilder;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isEmpty() {
		return fieldNames.isEmpty() && !cancelled;
	}

	public HierarchyType getHierarchyType() {
		return hierarchyType;
	}
}