package com.news.dtos;

import java.math.BigDecimal;

import com.news.enums.Language;

public record NewsPaperDto(
	    Long id,
	    String name,
	    Language language,
	    String description,
	    String publisher,
	    BigDecimal monthlyPrice,
	    BigDecimal yearlyPrice,
	    boolean isActive,
	    byte[] bytePhoto,
	    String photo // derived
	) {
	
}
