package com.news.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.models.Customer;
import com.news.models.Feedback;
import com.news.repositories.CustomerRepository;
import com.news.repositories.FeedbackRepository;

@Service
public class FeedbackService {

	@Autowired private FeedbackRepository fbRepo;	
	@Autowired private CustomerRepository customerRepo;
	
	
	public void submitCustomerFeedback(long customerId, short rating, String message) {
		
		Optional<Customer> cop = customerRepo.findById(customerId);
		
		if (cop.isPresent()) {
			Feedback fb = new Feedback();
			
			fb.setCustomer(cop.get());
			if (message != null && !message.isBlank()) {
				fb.setMessage(message);
			}
				
			fb.setRating(rating);
			
			fbRepo.save(fb);
		}
		
	}


	public List<Feedback> fetchCustomerFeedBacks(long id) {
		return fbRepo.findByCustomer_Id(id);
	}


	public List<Feedback> fetchAll() {
		// TODO Auto-generated method stub
		return fbRepo.findAll();
	}
	
	
}
