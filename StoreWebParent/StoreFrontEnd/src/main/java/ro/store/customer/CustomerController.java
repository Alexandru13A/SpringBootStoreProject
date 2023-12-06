package ro.store.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.Customer;
import ro.store.security.CustomerUserDetails;
import ro.store.security.oauth2.CustomerOAuth2User;
import ro.store.setting.EmailSettingBag;
import ro.store.setting.SettingService;
import ro.store.utility.Utility;

@Controller
public class CustomerController {

  @Autowired
  private SettingService settingService;

  private CustomerService customerService;

  @Autowired
  @Lazy
  public void setCustomerService(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {

    List<Country> listCountries = customerService.listAllCountries();

    model.addAttribute("listCountries", listCountries);
    model.addAttribute("pageTitle", "Customer Registration");
    model.addAttribute("customer", new Customer());

    return "/register/register_form";

  }

  @PostMapping("/create_customer")
  public String createCustomer(Customer customer, Model model, HttpServletRequest request)
      throws UnsupportedEncodingException, MessagingException {
    customerService.registerCustomer(customer);
    sendVerificationEmail(request, customer);

    model.addAttribute("pageTitle", "Registration Succeeded !");

    return "/register/register_success";
  }

  private void sendVerificationEmail(HttpServletRequest request, Customer customer)
      throws UnsupportedEncodingException, MessagingException {
    EmailSettingBag emailSettings = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

    String toAddress = customer.getEmail();
    String subject = emailSettings.getCustomerVerifySubject();
    String content = emailSettings.getCustomerVerifyContent();

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", customer.getFullName());

    String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

    content = content.replace("[[URL]]", verifyURL);

    System.out.println("Verify URL :" + verifyURL);

    helper.setText(content, true);

    mailSender.send(message);

    System.out.println("To address: " + toAddress);
    System.out.println("Verify URL: " + verifyURL);
  }

  @GetMapping("/verify")
  public String verifyAccount(@Param("code") String code, Model model) {
    boolean verified = customerService.verify(code);
    return "register/" + (verified ? "verify_success" : "verify_fail");
  }

  @GetMapping("/account_details")
  public String viewAccountDetails(Model model, HttpServletRequest request) {

    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    Customer customer = customerService.getCustomerByEmail(email);
    List<Country> listCountries = customerService.listAllCountries();

    model.addAttribute("customer", customer);
    model.addAttribute("listCountries", listCountries);
    model.addAttribute("pageTitle", "Account Details");

    return "customer/account_details";
  }

  // save the changes from the Customer account details
  @PostMapping("/update_account_details")
  public String updateAccountDetails(Model model, Customer customer, RedirectAttributes redirectAttributes,
      HttpServletRequest request) {

    customerService.updateCustomer(customer);
    redirectAttributes.addFlashAttribute("message", "Your account has been updated");

    updateNameForAuthenticatedCustomer(customer, request);

    String redirectOption = request.getParameter("redirect");
    String redirectURL = "redirect:/account_details";

    if ("address_book".equals(redirectOption)) {
      redirectURL = "redirect:/address_book";
    }else if("cart".equals(redirectOption)){
      redirectURL="redirect:/cart";
    }

    return redirectURL;
  }

  // Set the name from the authentication Facebook/Google with name from account
  // details form
  private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
    Object principal = request.getUserPrincipal();

    if (principal instanceof UsernamePasswordAuthenticationToken
        || principal instanceof RememberMeAuthenticationToken) {
      CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
      Customer authenticatedCustomer = userDetails.getCustomer();
      authenticatedCustomer.setFirstName(customer.getFirstName());
      authenticatedCustomer.setLastName(customer.getLastName());

    } else if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
      CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
      String fullName = customer.getFirstName() + " " + customer.getLastName();
      oAuth2User.setFullName(fullName);
    }
  }

  // Get name from authentication with Facebook/Google
  private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
    CustomerUserDetails userDetails = null;
    if (principal instanceof UsernamePasswordAuthenticationToken) {
      UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
      userDetails = (CustomerUserDetails) token.getPrincipal();
    } else if (principal instanceof RememberMeAuthenticationToken) {
      RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
      userDetails = (CustomerUserDetails) token.getPrincipal();
    }
    return userDetails;
  }

}
