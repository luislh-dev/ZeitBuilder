package com.zeitbuilder.zeitbuilder.domain.models;

public record FieldInfo(String name, boolean defaultSelected) {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private boolean defaultSelected;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder defaultSelected(boolean defaultSelected) {
			this.defaultSelected = defaultSelected;
			return this;
		}

		public FieldInfo build() {
			return new FieldInfo(name, defaultSelected);
		}
	}
}
