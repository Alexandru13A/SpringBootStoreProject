package ro.store.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bytebuddy.utility.RandomString;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.AuthenticationType;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;
import ro.store.countries_states_frontend.CountryRepository;

@Service
@Transactional
public class CustomerService {

  private final CountryRepository countryRepository;
  private final CustomerRepository customerRepository;

  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  public CustomerService(CountryRepository countryRepository, CustomerRepository customerRepository) {
    this.countryRepository = countryRepository;
    this.customerRepository = customerRepository;
  }

  public List<Country> listAllCountries() {
    return countryRepository.findAllByOrderByNameAsc();
  }

  public boolean isEmailUnique(String email) {
    Customer customer = customerRepository.findByEmail(email);
    return customer == null;
  }

  public void updateCustomer(Customer customerInForm) {
    Customer customerInDatabase = customerRepository.findById(customerInForm.getId()).get();

    if (customerInDatabase.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
      if (!customerInForm.getPassword().isEmpty()) {
        encodePassword(customerInForm);
      } else {
        customerInForm.setPassword(customerInDatabase.getPassword());
      }
    } else {
      customerInForm.setPassword(customerInDatabase.getPassword());
    }
    customerInForm.setEnabled(customerInDatabase.isEnabled());
    customerInForm.setCreatedTime(customerInDatabase.getCreatedTime());
    customerInForm.setVerificationCode(customerInDatabase.getVerificationCode());
    customerInForm.setAuthenticationType(customerInDatabase.getAuthenticationType());
    customerInForm.setResetPasswordToken(customerInDatabase.getResetPasswordToken());

    customerRepository.save(customerInForm);
  }

  public void registerCustomer(Customer customer) {
    encodePassword(customer);
    customer.setEnabled(false);
    customer.setCreatedTime(new Date());
    customer.setAuthenticationType(AuthenticationType.DATABASE);

    String randomCode = RandomString.make(64);
    customer.setVerificationCode(randomCode);

    customerRepository.save(customer);

    System.out.println("Verification code: " + randomCode);
  }

  public Customer getCustomerByEmail(String email) {
    return customerRepository.findByEmail(email);
  }

  private void encodePassword(Customer customer) {
    String encodedPassword = passwordEncoder.encode(customer.getPassword());
    customer.setPassword(encodedPassword);
  }

  public boolean verify(String verificationCode) {
    Customer customer = customerRepository.findByVerificationCode(verificationCode);

    if (customer == null || customer.isEnabled()) {
      return false;
    } else {
      customerRepository.enabled(customer.getId());
      return true;
    }
  }

  public void updateAuthenticationType(Customer customer, AuthenticationType type) {
    if (!customer.getAuthenticationType().equals(type)) {
      customerRepository.updateAuthenticationType(customer.getId(), type);
    }
  }

  public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
      AuthenticationType authenticationType) {
    Customer customer = new Customer();
    customer.setEmail(email);
    setName(name, customer);

    customer.setEnabled(true);
    customer.setCreatedTime(new Date());
    customer.setAuthenticationType(authenticationType);
    customer.setPassword("");
    customer.setAddressLine1("");
    customer.setCity("");
    customer.setState("");
    customer.setPhoneNumber("");
    customer.setPostalCode("");
    customer.setCountry(countryRepository.findByCode(countryCode));

    customerRepository.save(customer);
  }

  private void setName(String name, Customer customer) {
    String[] nameArray = name.split(" ");

    if (nameArray.length < 2) {
      customer.setFirstName(name);
      customer.setLastName("");
    } else {
      String firstName = nameArray[0];
      customer.setFirstName(firstName);

      String lastName = name.replaceFirst(firstName + " ", "");
      customer.setLastName(lastName);
    }
  }

  public String updateResetPasswordToken(String email) throws CustomerNotFoundException {

    Customer customer = customerRepository.findByEmail(email);
    if (customer != null) {
      String token = RandomString.make(30);
      customer.setResetPasswordToken(token);
      customerRepository.save(customer);

      return token;
    } else {
      throw new CustomerNotFoundException("Could not find any customer with the email " + email);
    }
  }

  public Customer getByResetPasswordToken(String token) {
    return customerRepository.findByResetPasswordToken(token);
  }

  public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
    Customer customer = customerRepository.findByResetPasswordToken(token);

    if (customer == null) {
      throw new CustomerNotFoundException("No customer found: Invalid token");
    }
    customer.setPassword(newPassword);
    customer.setResetPasswordToken(null);
    encodePassword(customer);
    
    customerRepository.save(customer);
  }

}
