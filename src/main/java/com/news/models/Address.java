package com.news.models;

import com.news.enums.State;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

	private String houseNumber;
	private String street;
	private String city;
	private String district;
	private String pincode;
	private String landmark;
	private String deliveryInstructions;
	
	@Enumerated(EnumType.STRING)
	private State state;
}
