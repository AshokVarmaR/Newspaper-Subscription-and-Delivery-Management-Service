package com.news.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.news.configurations.CustomUserDetails;
import com.news.dtos.NewsPaperDto;
import com.news.enums.State;
import com.news.enums.SubscriptionType;
import com.news.models.Customer;
import com.news.models.Delivery;
import com.news.models.Feedback;
import com.news.services.DeliveryService;
import com.news.services.FeedbackService;
import com.news.services.NewsPaperService;
import com.news.services.PaymentService;
import com.news.services.UserService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
    private NewsPaperService paperService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private FeedbackService fbService;

	@GetMapping("/signup")
	public String signup() {
		return "customer-signup";
	}
	
	private Customer getCurrentUser(Authentication authentication) {
	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	    String username = userDetails.getUsername();
	    Customer customer = userService.fetchByCustomerUsername(username);
	    return customer;
	}
	
	
	@PostMapping("/register")
	public String register(
			@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("phone") String phone, @RequestParam("username") String username,
			@RequestParam("password") String password, RedirectAttributes rab
			){
		boolean saved = userService.addCustomer(name,email,phone,username,password);
		
		if (saved) {
			rab.addFlashAttribute("error", "Username already exists...");
			return "redirect:/customer/login";
		}
		else return "redirect:/customer/signup";
	}
	
	@GetMapping("/login")
	public String login() {
		return "customer-login";
	}
	
	
	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication auth) {
		
		Customer customer = getCurrentUser(auth);
		List<NewsPaperDto> papers = paperService.fetchAll();
		
		
		List<Delivery> deliveries =  customer.getSubscriptions().stream().flatMap(sub -> sub.getDeliveries().stream()).collect(Collectors.toList());
		
		List<Delivery> recentDeliveries = customer.getSubscriptions().stream()
			    .flatMap(sub -> sub.getDeliveries().stream())
			    .sorted(Comparator.comparing(Delivery::getTime).reversed())
			    .limit(5)
			    .collect(Collectors.toList());
		
		List<Feedback> feedbacks = fbService.fetchCustomerFeedBacks(customer.getId());
		
		model.addAttribute("feedbacks", feedbacks);
		model.addAttribute("recentDeliveries", recentDeliveries);
		model.addAttribute("deliveries", deliveries);
		model.addAttribute("papers", papers.stream().filter(p -> p.isActive() == true));
		model.addAttribute("customer", getCurrentUser(auth));
		
		return "customer-dashboard";
	}
	
	
	@PostMapping("/address/add")
	public String addNewAddress(
	        @RequestParam("newHousenumber") String houseNumber,
	        @RequestParam("newStreet") String street,
	        @RequestParam("newCity") String city,
	        @RequestParam("newDistrict") String district,
	        @RequestParam("newState") State state,
	        @RequestParam("newPincode") String pincode,
	        @RequestParam(name = "newLandmark", required = false) String landmark,
	        @RequestParam(name = "newDeliveryInstructions", required = false) String deliveryInstructions,
	        Authentication auth,
	        RedirectAttributes redirectAttributes
	) {
		
		System.out.println("in address method");
	    try {
	    	Long customerId = getCurrentUser(auth).getId();
	        userService.addCustomerAddress(customerId, houseNumber, street, city, district, state, pincode, landmark, deliveryInstructions);
	        redirectAttributes.addFlashAttribute("successMessage", "Address added successfully!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to add address.");
	    }

	    return "redirect:/customer/dashboard"; // Adjust redirection as needed
	}

	
	
	@PostMapping("/address/update")
	public String updateAddress(
	        @RequestParam("customerId") Long customerId,
	        @RequestParam("houseNumber") String houseNumber,
	        @RequestParam("street") String street,
	        @RequestParam("city") String city,
	        @RequestParam("district") String district,
	        @RequestParam("state") State state,
	        @RequestParam("pincode") String pincode,
	        @RequestParam(name = "landmark", required = false) String landmark,
	        @RequestParam(name = "deliveryInstructions", required = false) String deliveryInstructions,
	        RedirectAttributes redirectAttributes
	) {
	    try {
	        userService.updateCustomerAddress(customerId, houseNumber, street, city, district, state, pincode, landmark, deliveryInstructions);
	        redirectAttributes.addFlashAttribute("successMessage", "Address updated successfully!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to update address.");
	    }

	    return "redirect:/customer/dashboard";
	}
	
	@PostMapping("card/add")
	public String addCard(
			@RequestParam("cardNumber") String cardNumber,
			@RequestParam("cardExpiry") String expiry,
			@RequestParam("cardCvv") String cvv,
			@RequestParam("cardName") String name, Authentication auth
			) {
		long customerId = getCurrentUser(auth).getId();
		paymentService.addCardDetails(customerId,cardNumber,name,cvv,expiry);
		return "redirect:/customer/dashboard";
	}
	
	
	@DeleteMapping("/card/delete/{cardId}")
	public ResponseEntity<?> deleteCard(@PathVariable Long cardId, Authentication auth){
		long customerId = getCurrentUser(auth).getId();
		paymentService.deleteCustomerCard(customerId,cardId);
		return ResponseEntity.ok("Card Deleted Succesfully");
	}
	
	
	@PostMapping("/newspaper/subscribe")
	public ResponseEntity<String> subscribe(
	    @RequestParam Long paperId,
	    @RequestParam String paperName,
	    @RequestParam SubscriptionType paperPlan,
	    @RequestParam BigDecimal paperPrice,
	    @RequestParam String cardId,
	    Authentication auth
	) {
	   long customerId = getCurrentUser(auth).getId();
		paperService.newSubscription(customerId, paperId,paperName,paperPlan, paperPrice);
	    return ResponseEntity.ok("Subscription successful");
	}
	
	@PostMapping("/profile/update")
	public String updateCustomerDetails(
			@RequestParam("name") String name,
			@RequestParam("email") String email,
			@RequestParam("phoneNumber") String phoneNumber,
			@RequestParam("dob") LocalDate dob,
			Authentication auth
			) {
		
		long customerId = getCurrentUser(auth).getId();
		userService.updateCustomerDetails(customerId,name,email,phoneNumber,dob);
		return "redirect:/customer/dashboard";
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
		long customerId = getCurrentUser(auth).getId();
		boolean changed = userService.changeCustomerPassword(customerId,currentPassword,newPassword);
		
		if (changed) {
			return ResponseEntity.ok("Password changed successfully");
		}
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" Your current password is wrong");
		
	}
	
	
	@PostMapping("/feedback/submit")
	public String submitFeedback(@RequestParam("rating") short rating, @RequestParam(name="message", required = false) String message, Authentication auth) {
		
		long customerId = getCurrentUser(auth).getId();
		fbService.submitCustomerFeedback(customerId,rating, message);
		return "redirect:/customer/dashboard";
	}
	
	
	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		return "forgot-password";
	}
	
	@PostMapping("/otp/send")
	public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    boolean isGenerated = userService.generateOtp(email);

	    Map<String, String> response = new HashMap<>();
	    
	    if (isGenerated) {
	        response.put("message", "OTP Generated Successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("error", "Failed to generate OTP");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}
	
	@PostMapping("/otp/verify")
	public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    String otp = jsonObject.get("otp");

	    boolean isVerified = userService.verifyOtp(email, otp);
	    Map<String, String> response = new HashMap<>();

	    if (isVerified) {
	        response.put("message", "OTP verified successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("message", "Invalid OTP");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}

	
	@PostMapping("/password/reset")
	public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    String password = jsonObject.get("password");

	    boolean isReset = userService.resetPassword(email, password);
	    Map<String, String> response = new HashMap<>();

	    if (isReset) {
	        response.put("message", "Password reset successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("message", "Password didn't reset");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}

}
