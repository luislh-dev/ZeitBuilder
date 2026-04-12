package com.zeitbuilder.zeitbuilder.model;

public class Storage {
	private boolean useInstanceBased = false;
	private HierarchyType hierarchyType = HierarchyType.STANDARD;

	public boolean getUseInstanceBased() {
		return useInstanceBased;
	}

	public void setUseInstanceBased(boolean useInstanceBased) {
		this.useInstanceBased = useInstanceBased;
	}

	public HierarchyType getHierarchyType() {
		return hierarchyType != null ? hierarchyType : HierarchyType.STANDARD;
	}

	public void setHierarchyType(HierarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
	}
}
