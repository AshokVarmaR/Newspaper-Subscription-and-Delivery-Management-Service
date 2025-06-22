package com.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Employee findByUserUsername(String username);

}
