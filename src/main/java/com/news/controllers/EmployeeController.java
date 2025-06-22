package com.news.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.news.configurations.CustomUserDetails;
import com.news.dtos.CustomerDto;
import com.news.dtos.DeliveryDto;
import com.news.dtos.EmployeeDto;
import com.news.dtos.NewsPaperDto;
import com.news.enums.DeliveryStatus;
import com.news.models.Customer;
import com.news.models.Delivery;
import com.news.models.Employee;
import com.news.models.NewsPaper;
import com.news.models.Subscription;
import com.news.repositories.EmployeeRepository;
import com.news.services.DeliveryService;
import com.news.services.NewsPaperService;
import com.news.services.UserService;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private UserService userService;

	@Autowired
	private NewsPaperService paperService;

	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private EmployeeRepository empRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private Employee getCurrentUser(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		Employee employee = userService.fetchByEmployeeUsername(username);
		return employee;
	}

	@PostMapping("/register")
	public String handleForm(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone,
			@RequestParam String username, @RequestParam String password, @RequestParam BigDecimal salary,

			@RequestParam String address, @RequestParam String city, @RequestParam String pincode,

			Model model) {

		userService.addEmployee(fullName, email, phone, username, password, salary, address, city, pincode);
		return "redirect:/admin/dashboard"; // returns to the same page or redirect elsewhere
	}

	@GetMapping("/login")
	public String loginPage() {
//		long id =1;
//		Optional<Employee> e = empRepo.findById(id);
//		if (e.isPresent()) {
//			e.get().getUser().setPassword(passwordEncoder.encode("1234"));
//			empRepo.save(e.get());
//		}
		return "employee-login";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication auth) {
		Employee emp = getCurrentUser(auth);
		
		
		List<Customer> customers = paperService.fetchCustomersOfEmployeePincode(emp.getId());

		LocalDate today = LocalDate.now();

		for (Customer c : customers) {
			for (Subscription sub : c.getSubscriptions()) {
				for (Delivery d : sub.getDeliveries()) {
					if (d.getTime().toLocalDate().equals(today) && d.getStatus() == null) {
						d.setStatus(DeliveryStatus.PENDING);
					}
				}
			}
		}
		
		Map<Long, String> customerStatusMap = new HashMap<>();

		for (Customer c : customers) {
		    boolean allDelivered = true;

		    for (Subscription sub : c.getSubscriptions()) {
		        boolean deliveredToday = sub.getDeliveries().stream()
		            .anyMatch(d -> d.getTime().toLocalDate().equals(today) && d.getStatus() == DeliveryStatus.DELIVERED);

		        if (!deliveredToday) {
		            allDelivered = false;
		            break;
		        }
		    }

		    customerStatusMap.put(c.getId(), allDelivered ? "DELIVERED" : "PENDING");
		}

	

		model.addAttribute("customerDeliveryStatus", customerStatusMap);
		model.addAttribute("customers", customers);
		model.addAttribute("employee", emp);
		return "employee-dashboard";
	}
	
	
	@GetMapping("/delivery/history")
	public ResponseEntity<?> fetchDeliveriesByDate(
	        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today,
	        Authentication auth) {

	    Employee emp = getCurrentUser(auth);
	    
	    EmployeeDto employee = new EmployeeDto(emp.getId(), emp.getEmployeeCode(), emp.getName(), emp.getPhoneNumber(), emp.getAddress(), emp.getCity(), emp.getPincode(), emp.getUser());
	    List<Customer> customers = paperService.fetchCustomersOfEmployeePincode(emp.getId());

	    List<DeliveryDto> result = new ArrayList<>();

	    for (Customer customer : customers) {
	    	
	    	CustomerDto cd = new CustomerDto(customer.getId(), customer.getName(), customer.getPhoneNumber(), customer.getAddress(), customer.getUser());
	    	
	    	
	    	
	    	List<NewsPaperDto> todaysPapers = new ArrayList<>();
	    	Set<Long> addedNewspaperIds = new HashSet<>(); // Track unique newspaper IDs

	    	DeliveryStatus status = DeliveryStatus.PENDING;
	    	LocalDateTime time = null;

	    	for (Subscription sub : customer.getSubscriptions()) {
	    	    for (Delivery d : sub.getDeliveries()) {
	    	        if (d.getTime().toLocalDate().equals(today)) {
	    	            NewsPaper np = sub.getNewspaper();

	    	            if (!addedNewspaperIds.contains(np.getId())) {
	    	                todaysPapers.add(new NewsPaperDto(
	    	                    np.getId(), np.getName(), np.getLanguage(), np.getDescription(),
	    	                    np.getPublisher(), np.getSubscriptionPrice().getMonthly(),
	    	                    np.getSubscriptionPrice().getYearly(), np.isActive(),
	    	                    np.getPhoto(), np.getB64Photo()
	    	                ));
	    	                addedNewspaperIds.add(np.getId());
	    	            }

	    	            if (d.getStatus() == DeliveryStatus.DELIVERED) {
	    	                status = DeliveryStatus.DELIVERED;
	    	            }

	    	            if (time == null) {
	    	                time = d.getTime();
	    	            }
	    	        }
	    	    }
	    	}

	        

	        if (!todaysPapers.isEmpty()) {
	            result.add(new DeliveryDto(cd, employee, status, time, todaysPapers));
	        }
	        
	        System.out.println();
	        for(var r : result) {
	        	System.out.println(r.newspapers().toString());
	        	System.out.println(r.newspapers().size());
	        }
	    }

	    return ResponseEntity.ok(result);
	}

	

	@PostMapping("/delivery/completed")
	public String markCustomerDeliveryCompleted(@RequestParam("customerId") Long customerId, Authentication auth) {

		deliveryService.markCustomerDeliveryCompleted(getCurrentUser(auth).getId(), customerId);
		return "redirect:/employee/dashboard";
	}
	
	
	@PostMapping("/password/change")
	public ResponseEntity<?> changePassword(
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			Authentication auth
			){
		
		System.out.println("Hi i am in password change method");
		System.out.println(currentPassword);
		System.out.println(newPassword);
		Employee emp = getCurrentUser(auth);
		boolean changed = userService.changeEmployeePassword(emp.getId(),currentPassword,newPassword);
		
		if (changed) {
			return ResponseEntity.ok("Password changed successfully");
		}
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" Your current password is wrong");
		
	}
	
	


}
