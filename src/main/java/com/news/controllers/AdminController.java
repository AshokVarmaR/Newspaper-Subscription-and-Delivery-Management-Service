package com.news.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.news.dtos.NewsPaperDto;
import com.news.enums.Language;
import com.news.models.Customer;
import com.news.models.Delivery;
import com.news.models.Employee;
import com.news.models.Subscription;
import com.news.services.DeliveryService;
import com.news.services.FeedbackService;
import com.news.services.NewsPaperService;
import com.news.services.PaymentService;
import com.news.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private NewsPaperService newspaperService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private FeedbackService fbService;
	
	@GetMapping("/login")
	public String login() {
		return "admin-login";
	}
	
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Customer> customers = userService.fetchAllCustomer();
		List<NewsPaperDto> papers = newspaperService.fetchAll(); 
		List<Employee> employees = userService.fetchAllEmployees();
		List<Subscription> subscriptions = paymentService.fetchAllSubscriptions();
		List<Delivery> deliveries = deliveryService.fetchAllSubscriptions();
		
		YearMonth currentMonth = YearMonth.now();

	    BigDecimal monthlyRevenue = subscriptions.stream()
	        .filter(sub -> {
	            LocalDateTime created = sub.getCreatedTime();
	            YearMonth subMonth = YearMonth.from(created);
	            return subMonth.equals(currentMonth);
	        })
	        .map(Subscription::getActualPrice)
	        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
	    model.addAttribute("feedbacks", fbService.fetchAll());
		model.addAttribute("monthlyRevenue", monthlyRevenue);
		model.addAttribute("deliveries", deliveries);
		model.addAttribute("subscriptions", subscriptions);
		model.addAttribute("employees", employees);
		model.addAttribute("languages", Language.values());
		model.addAttribute("papers", papers);
		model.addAttribute("customers", customers);
		return "admin-dashboard";
	}
	
	
	@PostMapping("/newspaper/add")
	public String addNewsPaper(
	        @RequestParam("name") String name,
	        @RequestParam("language") Language language,
	        @RequestParam("description") String description,
	        @RequestParam("image") MultipartFile image,
	        @RequestParam("publisher") String publisher,
	        @RequestParam("monthlyPrice") BigDecimal monthlyPrice,
	        @RequestParam("yearlyPrice") BigDecimal yearlyPrice,
	        @RequestParam(value = "activity") boolean isActive
	) throws IOException {
		
	    newspaperService.addNewsPaper(name, language, description, image, publisher, monthlyPrice, yearlyPrice, isActive);
	    return "redirect:/admin/dashboard";
	}
	
	
	@PostMapping("/employee/delete")
	public String deleteEmployee(@RequestParam("empId") Long empId) {
		userService.deleteEmployee(empId);
		return "redirect:/admin/dashboard";
	}
	
	@PostMapping("/employee/update")
	public String updateEmployeeDetails(
	        @RequestParam("id") Long id,
	        @RequestParam("name") String name,
	        @RequestParam("email") String email,
	        @RequestParam("phoneNumber") String phoneNumber,
	        @RequestParam("salary") BigDecimal salary,
	        @RequestParam("available") boolean available,
	        @RequestParam("address") String address,
	        @RequestParam("city") String city,
	        @RequestParam("pincode") String pincode
	) {
	    userService.updateEmployeeDetails(id, name, email, phoneNumber, salary, available, address, city, pincode);
	    
	    // Redirect or forward to employee list or confirmation page
	    return "redirect:/admin/dashboard"; // Change this to your actual view/page
	}
	
	@PostMapping("/newspaper/update")
	public String updateNewspaper(
	        @RequestParam("id") Long id,
	        @RequestParam("name") String name,
	        @RequestParam("language") Language language,
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "publisher", required = false) String publisher,
	        @RequestParam("monthlyPrice") BigDecimal monthlyPrice,
	        @RequestParam("yearlyPrice") BigDecimal yearlyPrice,
	        @RequestParam(value = "active", defaultValue = "false") boolean active,
	        @RequestParam(value = "image", required = false) MultipartFile imageFile) {

	    newspaperService.updateNewspaper(id, name, language, description, publisher, monthlyPrice, yearlyPrice, active, imageFile);
	    return "redirect:/admin/dashboard"; // Or wherever you want to go after saving
	}

	@DeleteMapping("/newspaper/delete/{id}")
	public ResponseEntity<?> deleteNewspaper(@PathVariable("id") Long paperId){
		newspaperService.deleteNewsPaper(paperId);
		return ResponseEntity.ok("Newspaper deleted Successfully");
	}


}
