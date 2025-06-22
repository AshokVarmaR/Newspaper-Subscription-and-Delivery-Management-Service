package com.news.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Customer findByUserUsername(String username);

	List<Customer> findByAddress_Pincode(String pincode);

}
