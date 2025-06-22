package com.news.enums;

public enum SubscriptionType {
    MONTHLY ("Monthly"),
    YEARLY ("Yearly");
    
    private final String displayName;
	
	SubscriptionType(String displayName){
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
