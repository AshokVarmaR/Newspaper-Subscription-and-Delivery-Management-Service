package com.news.enums;

public enum State {

	ANDHRA_PRADESH("Andhra Pradesh"),
	TELANGANA("Telangana");
	
	
	private final String displayName;
	
	State(String displayName) {
		this.displayName = displayName;
	}

	public String displayName() {
		return displayName;
	}
}
