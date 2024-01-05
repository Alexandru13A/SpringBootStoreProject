package ro.store.checkout.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.address.AddressService;
import ro.store.checkout.CheckoutInfo;
import ro.store.checkout.CheckoutService;
import ro.store.common.entity.Address;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.Customer.Customer;
import ro.store.customer.CustomerService;
import ro.store.shipping.ShippingRateService;
import ro.store.shoppingCart.ShoppingCartService;
import ro.store.utility.Utility;

@Controller
public class CheckoutController {

  private final CheckoutService checkoutService;
  private final CustomerService customerService;
  private final AddressService addressService;
  private final ShippingRateService shippingRateService;
  private final ShoppingCartService shoppingCartService;

  public CheckoutController(CheckoutService checkoutService, CustomerService customerService,
      AddressService addressService, ShippingRateService shippingRateService, ShoppingCartService shoppingCartService) {
    this.checkoutService = checkoutService;
    this.customerService = customerService;
    this.addressService = addressService;
    this.shippingRateService = shippingRateService;
    this.shoppingCartService = shoppingCartService;
  }

  @GetMapping("/checkout")
  public String showCheckoutPage(Model model, HttpServletRequest request) {

    Customer customer = getAuthenticatedCustomer(request);

    Address defaultAddress = addressService.getDefaultAddress(customer);
    ShippingRate shippingRate = null;

    if (defaultAddress != null) {
      model.addAttribute("shippingAddress", defaultAddress.toString());
      shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
    } else {
      model.addAttribute("shippingAddress", customer.getAddress());
      shippingRate = shippingRateService.getShippingRateForCustomer(customer);
    }

    if (shippingRate == null) {
      return "redirect:/cart";
    }

    List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
    CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

    model.addAttribute("checkoutInfo", checkoutInfo);
    model.addAttribute("cartItem", cartItems);

    return "checkout/checkout";
  }

  private Customer getAuthenticatedCustomer(HttpServletRequest request) {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    return customerService.getCustomerByEmail(email);
  }
}
