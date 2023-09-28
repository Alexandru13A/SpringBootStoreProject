package ro.store.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bytebuddy.utility.RandomString;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer;
import ro.store.countries_states_frontend.CountryRepository;

@Service
@Transactional
public class CustomerService {

  @Autowired
  private CountryRepository countryRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  PasswordEncoder passwordEncoder;

  public List<Country> listAllCountries() {
    return countryRepository.findAllByOrderByNameAsc();
  }

  public boolean isEmailUnique(String email) {
    Customer customer = customerRepository.findByEmail(email);
    return customer == null;
  }

  public void registerCustomer(Customer customer) {
    encodePassword(customer);
    customer.setEnabled(false);
    customer.setCreatedTime(new Date());

    String randomCode = RandomString.make(64);
    customer.setVerificationCode(randomCode);

    customerRepository.save(customer);

    System.out.println("Verification code: " + randomCode);
  }

  private void encodePassword(Customer customer) {
    String encodedPassword = passwordEncoder.encode(customer.getPassword());
    customer.setPassword(encodedPassword);
  }

  public boolean verify(String verificationCode){
   Customer customer = customerRepository.findByVerificationCode(verificationCode);

    if(customer == null || customer.isEnabled()){
      return false;
    }else{
      customerRepository.enabled(customer.getId());
      return true;
    }
  }

}
