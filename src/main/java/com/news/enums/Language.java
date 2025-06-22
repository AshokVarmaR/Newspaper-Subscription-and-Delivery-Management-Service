package com.news.enums;

public enum Language {

	TELUGU("Telugu"),
	ENGLISH("English"),
	HINDI("Hindi");
	
	private final String displayName;
	
	Language(String displayName){
		this.displayName = displayName;
	}
	
	public String displayName() {
		return displayName;
	}
	
}
