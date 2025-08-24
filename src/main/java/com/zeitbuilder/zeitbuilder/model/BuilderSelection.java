package com.zeitbuilder.zeitbuilder.model;

import java.util.List;

public class BuilderSelection {
	private final List<String> fieldNames;
	private final boolean useInstanceBased;
	private final boolean cancelled;

	public BuilderSelection(List<String> fieldNames, boolean useInstanceBased, boolean cancelled) {
		this.fieldNames = fieldNames;
		this.useInstanceBased = useInstanceBased;
		this.cancelled = cancelled;
	}

	public static BuilderSelection empty() {
		return new BuilderSelection(List.of(), false, false);
	}

	public static BuilderSelection cancelled() {
		return new BuilderSelection(List.of(), false, true);
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public boolean isUseInstanceBased() {
		return useInstanceBased;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isEmpty() {
		return fieldNames.isEmpty() && !cancelled;
	}
}