package ro.store.checkout.controller;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import ro.store.address.AddressService;
import ro.store.checkout.CheckoutInfo;
import ro.store.checkout.CheckoutService;
import ro.store.checkout.paypal.PayPalApiException;
import ro.store.checkout.paypal.PayPalService;
import ro.store.common.entity.Address;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.customer.Customer;
import ro.store.common.entity.order.Order;
import ro.store.common.entity.order.PaymentMethod;
import ro.store.customer.CustomerService;
import ro.store.order.OrderService;
import ro.store.setting.CurrencySettingBag;
import ro.store.setting.EmailSettingBag;
import ro.store.setting.PaymentSettingBag;
import ro.store.setting.SettingService;
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
  private final OrderService orderService;
  private final SettingService settingService;
  private final PayPalService payPalService;

  public CheckoutController(CheckoutService checkoutService, CustomerService customerService,
      AddressService addressService, ShippingRateService shippingRateService, ShoppingCartService shoppingCartService,
      OrderService orderService, SettingService settingService,PayPalService payPalService) {
    this.checkoutService = checkoutService;
    this.customerService = customerService;
    this.addressService = addressService;
    this.shippingRateService = shippingRateService;
    this.shoppingCartService = shoppingCartService;
    this.orderService = orderService;
    this.settingService = settingService;
    this.payPalService = payPalService;
  }

  // Checkout Page
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

    String currencyCode = settingService.getCurrencyCode();
    PaymentSettingBag paymentSettingBag = settingService.getPaymentSettings();
    String paypalClientId = paymentSettingBag.getClientID();

    
    model.addAttribute("customer", customer);
    model.addAttribute("paypalClientId", paypalClientId);
    model.addAttribute("currencyCode", currencyCode);
    model.addAttribute("checkoutInfo", checkoutInfo);
    model.addAttribute("cartItem", cartItems);

    return "checkout/checkout";
  }

  // Get the authenticated Customer/User
  private Customer getAuthenticatedCustomer(HttpServletRequest request) {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    return customerService.getCustomerByEmail(email);
  }

  // Place Order Method
  @PostMapping("/place_order")
  public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
    String paymentType = request.getParameter("paymentMethod");
    PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

    Customer customer = getAuthenticatedCustomer(request);

    Address defaultAddress = addressService.getDefaultAddress(customer);
    ShippingRate shippingRate = null;

    if (defaultAddress != null) {
      shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
    } else {
      shippingRate = shippingRateService.getShippingRateForCustomer(customer);
    }

    List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
    CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

    Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
    shoppingCartService.deleteByCustomer(customer);
    sendOrderConfirmationEmail(request, createdOrder);

    return "checkout/order_completed";
  }

  // Create and set Confirmation Email
  private void sendOrderConfirmationEmail(HttpServletRequest request, Order order)
      throws UnsupportedEncodingException, MessagingException {

    EmailSettingBag emailSettingBag = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);
    mailSender.setDefaultEncoding("utf-8");

    String toAddress = order.getCustomer().getEmail();
    String subject = emailSettingBag.getOrderConfirmationSubject();
    String content = emailSettingBag.getOrderConfirmationContent();

    subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message);

    messageHelper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
    messageHelper.setTo(toAddress);
    messageHelper.setSubject(subject);

    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MM yyyy");
    String orderTime = dateFormat.format(order.getOrderTime());

    CurrencySettingBag currencySettingBag = settingService.getCurrencySettings();
    String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettingBag);

    content = content.replace("[[name]]", order.getCustomer().getFullName());
    content = content.replace("[[orderId]]", String.valueOf(order.getId()));
    content = content.replace("[[orderTime]]", orderTime);
    content = content.replace("[[shippingAddress]]", order.getShippingAddress());
    content = content.replace("[[total]]", totalAmount);
    content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

    messageHelper.setText(content, true);
    mailSender.send(message);

  }

  @PostMapping("/process_paypal_order")
	public String processPayPalOrder(HttpServletRequest request, Model model) 
			throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");
		
		String pageTitle = "Checkout Failure";
		String message = null;
		
		try {
			if (payPalService.validateOrder(orderId)) {
				return placeOrder(request);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid";
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
		}
		
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
		
		return "message";
	}
}
