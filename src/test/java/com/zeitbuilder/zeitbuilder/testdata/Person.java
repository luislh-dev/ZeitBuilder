package com.zeitbuilder.zeitbuilder.testdata;

import java.io.Serial;
import java.io.Serializable;

public class Person implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String name;
	private int age;
	private String address;
	private boolean isActive;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

}
