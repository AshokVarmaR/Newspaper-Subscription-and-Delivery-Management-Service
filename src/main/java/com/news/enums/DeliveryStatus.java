package com.news.enums;

public enum DeliveryStatus {

	PENDING("Pending"),
	DELIVERED("Delivered"),
	FAILED("Failed");
	
	private final String displayName;
	
	DeliveryStatus(String displayName){
		this.displayName = displayName;
				
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
