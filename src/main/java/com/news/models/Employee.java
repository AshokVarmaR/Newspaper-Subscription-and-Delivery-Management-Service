package com.news.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="employees")
public class Employee {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String employeeCode;
	private String name;
	private String phoneNumber;
	private String address;
	private String city;
	private String pincode;
	
	private BigDecimal salary;
	
	@OneToMany(mappedBy="employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Delivery> deliveries = new ArrayList<>();
	
	private boolean available;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	
	public void setSalary(BigDecimal salary) {
		this.salary = salary.setScale(2, RoundingMode.HALF_UP);
	}
	
	
}
