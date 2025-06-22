package com.news.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.news.enums.DeliveryStatus;

public record DeliveryDto(
		CustomerDto customer,
		EmployeeDto employee,
		DeliveryStatus status,
		LocalDateTime time,
		List<NewsPaperDto> newspapers
		) {

}
