package com.news.models;

import java.time.LocalDateTime;

import com.news.enums.DeliveryStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="deliveries")
public class Delivery {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="subscription_id")
	private Subscription subscription;
	
	@ManyToOne
	@JoinColumn(name="employee_id")
	private Employee employee;
	
	
	@Enumerated(EnumType.STRING)
	private DeliveryStatus status;
	
	private LocalDateTime time;

}
