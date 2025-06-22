package com.news.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.enums.DeliveryStatus;
import com.news.models.Customer;
import com.news.models.Delivery;
import com.news.models.Employee;
import com.news.repositories.CustomerRepository;
import com.news.repositories.DeliveryRepository;
import com.news.repositories.EmployeeRepository;

@Service
public class DeliveryService {

	@Autowired
	private DeliveryRepository deliveryRepo;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private EmployeeRepository empRepo;

	public void markCustomerDeliveryCompleted(Long empId, Long customerId) {

		Optional<Customer> customer = customerRepo.findById(customerId);
		Optional<Employee> employee = empRepo.findById(empId);
		
		if (customer.isPresent() && employee.isPresent()) {
			Customer c = customer.get();
			
			
			for(var sub :c.getSubscriptions()) {
				Delivery d = new Delivery(); 
				d.setSubscription(sub);
				d.setEmployee(employee.get());
				d.setTime(LocalDateTime.now());
				d.setStatus(DeliveryStatus.DELIVERED);
				deliveryRepo.save(d);
			}
		}
	}

	public List<Delivery> fetchAllSubscriptions() {
		// TODO Auto-generated method stub
		return deliveryRepo.findAll();
	}
	
	
	
}