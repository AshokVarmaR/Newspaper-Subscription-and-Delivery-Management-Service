package com.news.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPrice {

    @Column(precision = 10, scale = 2)
    private BigDecimal monthly;

    @Column(precision = 10, scale = 2)
    private BigDecimal yearly;

    public void setMonthly(BigDecimal monthly) {
        this.monthly = monthly.setScale(2, RoundingMode.HALF_UP);
    }

    public void setYearly(BigDecimal yearly) {
        this.yearly = yearly.setScale(2, RoundingMode.HALF_UP);
    }
}
