package com.zeitbuilder.zeitbuilder.ui.models;

import java.util.List;

public class FieldSelectionResult {
	private final List<String> selectedFieldNames;
	private final boolean useInstanceBased;
	private final boolean cancelled;

	public FieldSelectionResult(List<String> selectedFieldNames, boolean useInstanceBased, boolean cancelled) {
		this.selectedFieldNames = selectedFieldNames;
		this.useInstanceBased = useInstanceBased;
		this.cancelled = cancelled;
	}

	public static FieldSelectionResult cancelled() {
		return new FieldSelectionResult(List.of(), false, true);
	}

	public List<String> getSelectedFieldNames() { return selectedFieldNames; }
	public boolean isUseInstanceBased() { return useInstanceBased; }
	public boolean isCancelled() { return cancelled; }
}
