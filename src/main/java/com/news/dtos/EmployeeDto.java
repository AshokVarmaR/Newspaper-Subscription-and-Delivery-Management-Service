package com.news.dtos;

import com.news.models.User;

public record EmployeeDto(
		
		long id,
		String employeeCode,
		String name,
		String phoneNumber,
		String address,
		String city,
		String pincode,
		
		User user
		
		) {


}
