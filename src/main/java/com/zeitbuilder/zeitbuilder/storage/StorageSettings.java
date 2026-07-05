package com.zeitbuilder.zeitbuilder.storage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.zeitbuilder.zeitbuilder.model.HierarchyType;
import org.jetbrains.annotations.NotNull;

@Service
@State(
	name = "ZeitBuilderGeneratorSettings",
	storages = @com.intellij.openapi.components.Storage("zeitBuilderSettings.xml")
)
public final class StorageSettings implements PersistentStateComponent<Storage> {
	private final Storage storage = new Storage();

	public boolean isUseInstanceBased() {
		return storage.getUseInstanceBased();
	}

	public void setUseInstanceBased(boolean value) {
		storage.setUseInstanceBased(value);
	}

	public HierarchyType getHierarchyType() {
		return storage.getHierarchyType();
	}

	public void setHierarchyType(HierarchyType hierarchyType) {
		storage.setHierarchyType(hierarchyType);
	}

	@Override
	public @NotNull Storage getState() {
		return storage;
	}

	@Override
	public void loadState(@NotNull Storage storage) {
		this.storage.setUseInstanceBased(storage.getUseInstanceBased());
		this.storage.setHierarchyType(storage.getHierarchyType());
	}
}
