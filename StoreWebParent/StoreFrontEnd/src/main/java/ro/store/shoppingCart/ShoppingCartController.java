package ro.store.shoppingCart;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.address.AddressService;
import ro.store.common.entity.Address;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.Customer.Customer;
import ro.store.customer.CustomerService;
import ro.store.shipping.ShippingRateService;
import ro.store.utility.Utility;

@Controller
public class ShoppingCartController {

  private ShoppingCartService cartService;
  private CustomerService customerService;
  private AddressService addressService;
  private ShippingRateService shippingRateService;

  public ShoppingCartController(ShoppingCartService cartService, CustomerService customerService,
      AddressService addressService, ShippingRateService shippingRateService) {
    this.addressService = addressService;
    this.shippingRateService = shippingRateService;
    this.cartService = cartService;
    this.customerService = customerService;
  }

  @GetMapping("/cart")
  public String viewCart(Model model, HttpServletRequest request) {

    Customer customer = getAuthenticatedCustomer(request);
    List<CartItem> cartItems = cartService.listCartItems(customer);

    float estimatedTotal = 0.0F;
    for (CartItem item : cartItems) {
      estimatedTotal += item.getSubtotal();
    }

    Address defaultAddress = addressService.getDefaultAddress(customer);
    ShippingRate shippingRate = null;
    boolean usePrimaryAddressAsDefault = false;

    if (defaultAddress != null) {
      shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
    } else {
      usePrimaryAddressAsDefault = true;
      shippingRate = shippingRateService.getShippingRateForCustomer(customer);
    }

    model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
    model.addAttribute("shippingSupported", shippingRate != null);
    model.addAttribute("cartItems", cartItems);
    model.addAttribute("estimatedTotal", estimatedTotal);

    return "/cart/shopping_cart";
  }

  private Customer getAuthenticatedCustomer(HttpServletRequest request) {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    return customerService.getCustomerByEmail(email);

  }

}
