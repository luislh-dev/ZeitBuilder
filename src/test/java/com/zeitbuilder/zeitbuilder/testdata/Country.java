package com.zeitbuilder.zeitbuilder.testdata;

public record Country(String name, String code) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().name(this.name()).code(this.code());
    }

    public static class Builder {
        private String name;
        private String code;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Country build() {
            return new Country(name, code);
        }
    }
}
