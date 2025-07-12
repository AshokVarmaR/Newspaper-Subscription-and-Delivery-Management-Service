package com.news.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.news.enums.State;
import com.news.enums.UserRole;
import com.news.models.Address;
import com.news.models.Customer;
import com.news.models.Employee;
import com.news.models.Otp;
import com.news.models.User;
import com.news.repositories.CustomerRepository;
import com.news.repositories.EmployeeRepository;
import com.news.repositories.OtpRepository;
import com.news.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private OtpRepository otpRepo;
	
	
	@Value("${spring.mail.username}")
	private String mailSenderUsername;
	
	@Value("${spring.mail.password}")
	private String mailSenderPassword;

	public boolean addCustomer(String name, String email, String phone, String username, String password) {

		Optional<User> usop = userRepo.findByUsername(username);

		if (usop.isEmpty()) {

			User user = new User();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(UserRole.CUSTOMER);
			user.setEmail(email);
			userRepo.save(user);

			Customer customer = new Customer();
			customer.setPhoneNumber(phone);
			customer.setName(name);
			customer.setUser(user);
			customerRepo.save(customer);
			return true;
		}
		return false;
	}

	public Customer fetchByCustomerUsername(String username) {

		Customer customer = customerRepo.findByUserUsername(username);

		return customer;
	}

	public List<Customer> fetchAllCustomer() {

		return customerRepo.findAll();
	}

	public void addCustomerAddress(Long customerId, String houseNumber, String street, String city, String district,
			State state, String pincode, String landmark, String deliveryInstructions) {

		Optional<Customer> cop = customerRepo.findById(customerId);

		if (cop.isPresent()) {
			Address ad = new Address();
			ad.setHouseNumber(houseNumber);
			ad.setStreet(street);
			ad.setCity(city);
			ad.setDistrict(district);
			ad.setDistrict(district);
			ad.setState(state);
			ad.setPincode(pincode);
			ad.setLandmark(landmark);
			ad.setDeliveryInstructions(deliveryInstructions);

			cop.get().setAddress(ad);
			customerRepo.save(cop.get());
		}

	}

	public void updateCustomerAddress(Long customerId, String houseNumber, String street, String city, String district,
			State state, String pincode, String landmark, String deliveryInstructions) {

		Optional<Customer> cop = customerRepo.findById(customerId);

		if (cop.isPresent()) {
			Customer customer = cop.get();

			Address existingAddress = customer.getAddress();
			if (existingAddress == null) {
				existingAddress = new Address(); // Create new if not exists
			}

			if (houseNumber != null && !houseNumber.isEmpty()
					&& !houseNumber.equals(existingAddress.getHouseNumber())) {
				existingAddress.setHouseNumber(houseNumber);
			}

			if (street != null && !street.isEmpty() && !street.equals(existingAddress.getStreet())) {
				existingAddress.setStreet(street);
			}

			if (city != null && !city.isEmpty() && !city.equals(existingAddress.getCity())) {
				existingAddress.setCity(city);
			}

			if (district != null && !district.isEmpty() && !district.equals(existingAddress.getDistrict())) {
				existingAddress.setDistrict(district);
			}

			if (state != null && !state.equals(existingAddress.getState())) {
				existingAddress.setState(state);
			}

			if (pincode != null && !pincode.isEmpty() && !pincode.equals(existingAddress.getPincode())) {
				existingAddress.setPincode(pincode);
			}

			if (landmark != null && !landmark.isEmpty() && !landmark.equals(existingAddress.getLandmark())) {
				existingAddress.setLandmark(landmark);
			}

			if (deliveryInstructions != null && !deliveryInstructions.isEmpty()
					&& !deliveryInstructions.equals(existingAddress.getDeliveryInstructions())) {
				existingAddress.setDeliveryInstructions(deliveryInstructions);
			}

			customer.setAddress(existingAddress);
			customerRepo.save(customer);
		}
	}

	public void addEmployee(String fullName, String email, String phone, String username, String password,
			BigDecimal salary, String address, String city, String pincode) {

		Employee emp = new Employee();
		User user = new User();

		// 1. Create and save User
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(UserRole.EMPLOYEE);
		userRepo.save(user); // Save user first so it gets an ID if needed

		// 2. Set employee details
		emp.setUser(user);
		emp.setName(fullName);
		emp.setAddress(address);
		emp.setCity(city);
		emp.setPhoneNumber(phone);
		emp.setPincode(pincode);
		emp.setAvailable(true);
		emp.setSalary(salary);

		// 3. Save employee to get ID
		emp = empRepo.save(emp); // This assigns ID (e.g., 3)

		// 4. Generate employee code from ID
		String code = String.format("NPDE-%04d", emp.getId());
		emp.setEmployeeCode(code);

		// 5. Save again to persist the employee code
		emp = empRepo.save(emp);

	}

	public List<Employee> fetchAllEmployees() {
		// TODO Auto-generated method stub
		return empRepo.findAll();
	}

	public Employee fetchByEmployeeUsername(String username) {
		// TODO Auto-generated method stub
		return empRepo.findByUserUsername(username);
	}

	public boolean changeEmployeePassword(long id, String currentPassword, String newPassword) {
		Optional<Employee> employee = empRepo.findById(id);

		if (employee.isPresent()) {
			Employee emp = employee.get();
			if (passwordEncoder.matches(currentPassword, emp.getUser().getPassword())) {
				emp.getUser().setPassword(passwordEncoder.encode(newPassword));
				empRepo.save(emp);
				return true;
			}
		}
		return false;
	}

	public void updateCustomerDetails(long customerId, String name, String email, String phoneNumber, LocalDate dob) {

		Optional<Customer> cop = customerRepo.findById(customerId);

		if (cop.isPresent()) {
			Customer cust = cop.get();
			if (name != null && !name.isEmpty() && !name.isBlank())
				cust.setName(name);
			if (phoneNumber != null && !phoneNumber.isEmpty() && !phoneNumber.isBlank())
				cust.setPhoneNumber(phoneNumber);
			if (dob != null)
				cust.setDob(dob);
			if (email != null && !email.isEmpty() && !email.isBlank())
				cust.getUser().setEmail(email);

			customerRepo.save(cust);

		}

	}

	public boolean changeCustomerPassword(long customerId, String currentPassword, String newPassword) {
		Optional<Customer> customer = customerRepo.findById(customerId);

		if (customer.isPresent()) {
			Customer cust = customer.get();
			if (passwordEncoder.matches(currentPassword, cust.getUser().getPassword())) {
				cust.getUser().setPassword(passwordEncoder.encode(newPassword));
				customerRepo.save(cust);
				return true;
			}
		}
		return false;
	}

	public void deleteEmployee(Long empId) {
		Optional<Employee> emp = empRepo.findById(empId);

		if (emp.isPresent()) {
			empRepo.delete(emp.get());
		}
	}

	public void updateEmployeeDetails(Long id, String name, String email, String phoneNumber, BigDecimal salary,
			boolean available, String address, String city, String pincode) {

		empRepo.findById(id).ifPresent(emp -> {

			if (name != null && !name.isBlank() && !emp.getName().equals(name)) {
				emp.setName(name);
			}

			if (email != null && !email.isBlank() && !emp.getUser().getEmail().equals(email)) {
				emp.getUser().setEmail(email);
			}

			if (phoneNumber != null && !phoneNumber.isBlank() && !emp.getPhoneNumber().equals(phoneNumber)) {
				emp.setPhoneNumber(phoneNumber);
			}

			if (salary != null) {
				if (emp.getSalary() == null || emp.getSalary().compareTo(salary) != 0) {
					emp.setSalary(salary);
				}
			}

			emp.setAvailable(available);

			if (address != null && !address.isBlank() && !emp.getAddress().equals(address)) {
				emp.setAddress(address);
			}

			if (city != null && !city.isBlank() && !emp.getCity().equals(city)) {
				emp.setCity(city);
			}

			if (pincode != null && !pincode.isBlank() && !emp.getPincode().equals(pincode)) {
				emp.setPincode(pincode);
			}

			empRepo.save(emp);
		});
	}

	public boolean generateOtp(String email) {
	    Optional<User> optionalUser = userRepo.findByEmail(email);
	    
	    if (optionalUser.isPresent()) {
	        User user = optionalUser.get();
	        
	        if (user.getRole().equals(UserRole.CUSTOMER)) {
	            // Generate 6-digit OTP
	            int otp = 100000 + new Random().nextInt(900000);
	            String otpString = Integer.toString(otp);

	            // Delete existing OTP for this user
	            otpRepo.findByUser_Id(user.getId()).ifPresent(otpRepo::delete);

	            // Create and save new OTP
	            Otp otpass = new Otp();
	            otpass.setPassword(passwordEncoder.encode(otpString)); 
	            otpass.setCreatedAt(LocalDateTime.now());
	            otpass.setUser(user); 
	            otpRepo.save(otpass); 

	            // Send OTP via email
	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setFrom(mailSenderUsername);
	            message.setTo(email);
	            message.setSubject("Password Reset");
	            message.setText("OTP to reset your Newspaper Account password is " + otp +". Do not share this with anyone.");
	            mailSender.send(message);
	            System.out.println("Otp : "+otp);
	            return true;
	        }
	    }

	    return false;
	}

	public boolean verifyOtp(String email, String otp) {
	    Optional<Otp> optionalOtp = otpRepo.findByUser_Email(email);
	    System.out.println("Iam in verify method");
	    if (optionalOtp.isPresent()) {
	        Otp ot = optionalOtp.get();
	        if (passwordEncoder.matches(otp, ot.getPassword())) {
	            return true;
	        }
	    }

	    return false;
	}

	public boolean resetPassword(String email, String password) {
		
		Optional<Otp> optionalOtp = otpRepo.findByUser_Email(email);
		if(optionalOtp.isPresent()){
			Otp otp = optionalOtp.get();
			otp.getUser().setPassword(passwordEncoder.encode(password));
			otpRepo.save(otp);
			return true;
		}		
		return false;
		
		

	}




}