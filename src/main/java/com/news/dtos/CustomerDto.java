package com.news.dtos;

import com.news.models.Address;
import com.news.models.User;

import jakarta.persistence.Embedded;

public record CustomerDto(

		long id,
		String name,
		String phoneNumber,
		
		@Embedded
		Address address,
		
		User user
		) {

}
