package com.news.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.models.Card;
import com.news.models.Customer;
import com.news.models.Subscription;
import com.news.repositories.CardRepository;
import com.news.repositories.CustomerRepository;
import com.news.repositories.SubscriptionRepository;

@Service
public class PaymentService {

	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CardRepository cardRepo;
	
	@Autowired
	private SubscriptionRepository subRepo;

	public void addCardDetails(long customerId, String cardNumber, String name, String cvv, String expiry) {
		
		Optional<Customer> cop = customerRepo.findById(customerId);
		if (cop.isPresent()) {
			Card card = new Card();
			card.setCardHolderName(name);
			card.setCardNumber(cardNumber);
			card.setExpiryDate(expiry);
			card.setCvv(cvv);
			card.setCustomer(cop.get());
			
			cardRepo.save(card);
			
		}
		
	}

	public List<Subscription> fetchAllSubscriptions() {

		return subRepo.findAll();
	}

	public void deleteCustomerCard(long customerId, Long cardId) {
		
		cardRepo.findById(cardId).ifPresent(card ->{
			if(card.getCustomer().getId() == customerId) {
				cardRepo.delete(card);
			}
		});
	}
	
}
