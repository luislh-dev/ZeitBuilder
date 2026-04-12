package com.zeitbuilder.zeitbuilder.ui.models;

import com.zeitbuilder.zeitbuilder.model.HierarchyType;

import java.util.Collections;
import java.util.List;

public class FieldSelectionResult {

	private final List<String> selectedFieldNames;
	private final boolean useInstanceBased;
	private final boolean isCancelled;
	private final HierarchyType hierarchyType;

	public FieldSelectionResult(List<String> selectedFieldNames, boolean useInstanceBased, boolean isCancelled, HierarchyType hierarchyType) {
		this.selectedFieldNames = selectedFieldNames;
		this.useInstanceBased = useInstanceBased;
		this.isCancelled = isCancelled;
		this.hierarchyType = hierarchyType;
	}

	public static FieldSelectionResult cancelled() {
		return new FieldSelectionResult(Collections.emptyList(), false, true, HierarchyType.STANDARD);
	}

	public List<String> getSelectedFieldNames() {
		return selectedFieldNames;
	}

	public boolean isUseInstanceBased() {
		return useInstanceBased;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public HierarchyType getHierarchyType() {
		return hierarchyType;
	}
}
