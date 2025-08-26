package com.zeitbuilder.zeitbuilder.testdata;

import java.io.Serial;
import java.io.Serializable;

public final class Person implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final String name;
	private final int age;
	private final String address;
	private final boolean isActive;

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public String getAddress() {
		return address;
	}

	public boolean isActive() {
		return isActive;
	}

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.address = builder.address;
        this.isActive = builder.isActive;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().name(this.name).age(this.age).address(this.address).isActive(this.isActive);
    }

    public static class Builder {
        private String name;
        private int age;
        private String address;
        private boolean isActive;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
