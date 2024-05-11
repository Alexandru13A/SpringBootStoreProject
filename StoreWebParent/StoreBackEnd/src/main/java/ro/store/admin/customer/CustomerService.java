package ro.store.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.countries_states_backend.CountryRepository;
import ro.store.common.entity.Country;
import ro.store.common.entity.customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {

  public static final int CUSTOMER_PER_PAGE = 10;

  private final CustomerRepository customerRepository;
  private final CountryRepository countryRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository) {
    this.customerRepository = customerRepository;
    this.countryRepository = countryRepository;
  }

  public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMER_PER_PAGE, customerRepository);
	}

  public void saveCustomer(Customer customerInForm) {
    Customer customerInDatabase = customerRepository.findById(customerInForm.getId()).get();

    if (!customerInForm.getPassword().isEmpty()) {
      encodePassword(customerInForm);
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

  public void deleteCustomer(Integer id) throws CustomerNotFoundException {
    Long countById = customerRepository.countById(id);

    if (countById == null || countById == 0) {
      throw new CustomerNotFoundException("Could not found any customer with ID: " + id);
    }
    customerRepository.deleteById(id);
  }

  private void encodePassword(Customer customer) {
    String encodedPassword = passwordEncoder.encode(customer.getPassword());
    customer.setPassword(encodedPassword);
  }

  public Customer get(Integer id) throws CustomerNotFoundException {
    try {
      return customerRepository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new CustomerNotFoundException("Could not found any product with ID: " + id);
    }
  }

  public boolean checkForUniqueEmail(Integer id, String email) {
    Customer existCustomer = customerRepository.findByEmail(email);

    if (existCustomer != null && existCustomer.getId() != id) {
      return false;
    }
    return true;
  }

  public void updateStatus(Integer id, boolean enabled) {
    customerRepository.updateCustomerStatus(id, enabled);
  }

  public List<Country> listAllCountries() {
    return countryRepository.findAllByOrderByNameAsc();
  }

}
