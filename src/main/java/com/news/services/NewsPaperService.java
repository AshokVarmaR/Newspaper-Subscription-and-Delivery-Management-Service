package com.news.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.news.dtos.NewsPaperDto;
import com.news.enums.Language;
import com.news.enums.SubscriptionType;
import com.news.models.Customer;
import com.news.models.Employee;
import com.news.models.NewsPaper;
import com.news.models.Subscription;
import com.news.models.SubscriptionPrice;
import com.news.repositories.CustomerRepository;
import com.news.repositories.EmployeeRepository;
import com.news.repositories.NewsPaperRepository;
import com.news.repositories.SubscriptionRepository;
//import com.news.repositories.SubscriptionRepository;

@Service
public class NewsPaperService {

	@Autowired
	private NewsPaperRepository paperRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private SubscriptionRepository subRepo;

	@Autowired
	private EmployeeRepository empRepo;

	public void addNewsPaper(String name, Language language, String description, MultipartFile image, String publisher,
			BigDecimal monthlyPrice, BigDecimal yearlyPrice, boolean isActive) throws IOException {

		Optional<NewsPaper> np = paperRepo.findByNameIgnoreCase(name);
		System.out.println("Hi I am here...2");
		if (np.isEmpty()) {
			NewsPaper paper = new NewsPaper();
			paper.setName(name);
			paper.setLanguage(language);
			paper.setDescription(description);
			paper.setPhoto(image.getBytes());
			paper.setPublisher(publisher);

			SubscriptionPrice price = new SubscriptionPrice();
			price.setMonthly(monthlyPrice);
			price.setYearly(yearlyPrice);
			paper.setSubscriptionPrice(price);

			paper.setActive(isActive);
			System.out.println("Hi I am here...3");
			paperRepo.save(paper);
		}

	}

	public List<NewsPaperDto> fetchAll() {

		List<NewsPaperDto> papers = new ArrayList<>();
		for (var p : paperRepo.findAll()) {

			String photo = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(p.getPhoto());

			NewsPaperDto dto = new NewsPaperDto(p.getId(), p.getName(), p.getLanguage(), p.getDescription(),
					p.getPublisher(), p.getSubscriptionPrice().getMonthly(), p.getSubscriptionPrice().getYearly(),
					p.isActive(), p.getPhoto(), photo);

			papers.add(dto);

		}

		return papers;
	}

	public void newSubscription(long customerId, Long paperId, String paperName, SubscriptionType paperPlan,
			BigDecimal paperPrice) {

		Optional<Customer> customer = customerRepo.findById(customerId);
		Optional<NewsPaper> paper = paperRepo.findById(paperId);

		if (customer.isPresent() && paper.isPresent()) {
			Subscription sub = new Subscription();

			sub.setNewspaper(paper.get());
			sub.setCustomer(customer.get());
			sub.setCreatedTime(LocalDateTime.now());
			sub.setActualPrice(paperPrice);
			sub.setStartDate(LocalDate.now().plusDays(1));
			sub.setType(paperPlan);
			if (sub.getType().equals(SubscriptionType.MONTHLY)) {
				sub.setEndDate(sub.getStartDate().plusMonths(1));
			} else if (sub.getType().equals(SubscriptionType.YEARLY)) {
				sub.setEndDate(sub.getStartDate().plusYears(1));
			}
			sub.setStatus(true);

			subRepo.save(sub);
		}

	}

	public List<Customer> fetchCustomersOfEmployeePincode(long id) {
		Optional<Employee> empOp = empRepo.findById(id);
		if (empOp.isPresent()) {
			Employee emp = empOp.get();
			List<Customer> customers = customerRepo.findByAddress_Pincode(emp.getPincode());
			customers = customers.stream().filter(c -> !c.getSubscriptions().isEmpty()).collect(Collectors.toList());
			return customers;
		}

		return null;
	}

	public void updateNewspaper(Long id, String name, Language language, String description, String publisher,
			BigDecimal monthlyPrice, BigDecimal yearlyPrice, boolean active, MultipartFile imageFile) {

		paperRepo.findById(id).ifPresent(paper -> {

			// Handle image update
			if (imageFile != null && !imageFile.isEmpty()) {
				try {
					paper.setPhoto(imageFile.getBytes());
				} catch (IOException e) {
					e.printStackTrace(); // Consider logging in production
				}
			}

			// Update name
			if (name != null && !name.isBlank() && !paper.getName().equals(name)) {
				paper.setName(name);
			}

			// Update language
			if (language != null && !paper.getLanguage().equals(language)) {
				paper.setLanguage(language);
			}

			// Update description
			if (description != null && !description.isBlank()
					&& (paper.getDescription() == null || !paper.getDescription().equals(description))) {
				paper.setDescription(description);
			}

			// Update publisher
			if (publisher != null && !publisher.isBlank()
					&& (paper.getPublisher() == null || !paper.getPublisher().equals(publisher))) {
				paper.setPublisher(publisher);
			}

			// Update monthlyPrice
			if (monthlyPrice != null && (paper.getSubscriptionPrice().getMonthly() == null
					|| paper.getSubscriptionPrice().getMonthly().compareTo(monthlyPrice) != 0)) {
				paper.getSubscriptionPrice().setMonthly(monthlyPrice);
			}

			// Update yearlyPrice
			if (yearlyPrice != null && (paper.getSubscriptionPrice().getYearly() == null
					|| paper.getSubscriptionPrice().getYearly().compareTo(yearlyPrice) != 0)) {
				paper.getSubscriptionPrice().setYearly(yearlyPrice);
			}

			// Update active status
			if (paper.isActive() != active) {
				paper.setActive(active);
			}

			// Save the updated paper
			paperRepo.save(paper);
		});
	}

	public void deleteNewsPaper(Long paperId) {
		
		paperRepo.findById(paperId).ifPresent(paper -> {
			paperRepo.delete(paper);
		});
		
	}

}
