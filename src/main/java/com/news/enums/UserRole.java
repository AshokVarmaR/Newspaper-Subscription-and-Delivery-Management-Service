package com.news.enums;

public enum UserRole {

	ADMIN("Admin"),
	EMPLOYEE("Employee"),
	CUSTOMER("Customer");
	
	private final String displayName;
	
	UserRole(String displayName) {
		this.displayName = displayName;
	}

	public String displayName() {
		return displayName;
	}
}
