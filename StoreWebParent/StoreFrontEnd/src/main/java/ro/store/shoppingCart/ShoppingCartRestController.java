package ro.store.shoppingCart;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;
import ro.store.customer.CustomerService;
import ro.store.utility.Utility;

@RestController
public class ShoppingCartRestController {

  private ShoppingCartService cartService;
  private CustomerService customerService;

  public ShoppingCartRestController(ShoppingCartService cartService, CustomerService customerService) {
    this.cartService = cartService;
    this.customerService = customerService;
  }

  @PostMapping("/cart/add/{productId}/{quantity}")
  public String addProductToCart(@PathVariable(name = "productId") Integer productId,
      @PathVariable("quantity") Integer quantity, HttpServletRequest request) {

    try {
      Customer customer = getAuthenticatedCustomer(request);
      Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);
      return updatedQuantity + " item(s) of this product were added to the shopping cart";
    } catch (CustomerNotFoundException ex) {
      return "You must login to add this product to cart.";
    } catch (ShoppingCartException ex) {
      return ex.getMessage();
    }
  }

  private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    if (email == null) {
      throw new CustomerNotFoundException("No authenticated customer");
    }
    return customerService.getCustomerByEmail(email);

  }

  @PostMapping("/cart/update/{productId}/{quantity}")
  public String updateQuantity(@PathVariable(name = "productId") Integer productId,
      @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
    try {
      Customer customer = getAuthenticatedCustomer(request);
      float subtotal = cartService.updatedQuantity(productId, quantity, customer);
      return String.valueOf(subtotal);

    } catch (CustomerNotFoundException ex) {
      return "You must login to add this product to cart.";
    }
  }

  @DeleteMapping("/cart/remove/{productId}")
  public String removeProduct(HttpServletRequest request, @PathVariable("productId")Integer productId){

      try {
         Customer customer = getAuthenticatedCustomer(request);
         cartService.removeProduct(customer, productId);
         return "Successfully removed.";
      } catch (CustomerNotFoundException e) {
       return "You most login to remove product";
      }



  
  }

}
