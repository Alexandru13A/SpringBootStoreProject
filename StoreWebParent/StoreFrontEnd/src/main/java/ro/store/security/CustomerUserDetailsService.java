package ro.store.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ro.store.common.entity.customer.Customer;
import ro.store.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByEmail(email);
    if (customer == null) {
      throw new UsernameNotFoundException("No customer found with the email " + email);
    }
    return new CustomerUserDetails(customer);

  }

}
