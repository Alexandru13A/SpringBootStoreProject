package ro.store.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ro.store.common.entity.Customer.AuthenticationType;
import ro.store.common.entity.Customer.Customer;
import ro.store.customer.CustomerService;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private CustomerService customerService;

  public void setCustomerService(CustomerService customerService){
    this.customerService = customerService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authentication) throws IOException, ServletException {
        
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        Customer customer = userDetails.getCustomer();
        customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);

    super.onAuthenticationSuccess(request, response, chain, authentication);
  }
  
  
}
