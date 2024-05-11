package ro.store.customer;

import java.io.UnsupportedEncodingException;

import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import ro.store.common.entity.customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;
import ro.store.setting.EmailSettingBag;
import ro.store.setting.SettingService;
import ro.store.utility.Utility;

@Controller
public class ForgotPasswordController {

  private final CustomerService customerService;
  private final SettingService settingService;

  public ForgotPasswordController(CustomerService customerService, SettingService settingService) {
    this.customerService = customerService;
    this.settingService = settingService;
  }

  @GetMapping("/forgot_password")
  public String showRequestForm() {
    return "customer/forgot_password_form";
  }

  @PostMapping("/forgot_password")
  public String processRequestForm(HttpServletRequest request, Model model) {
    String email = request.getParameter("email");

    try {
      String token = customerService.updateResetPasswordToken(email);
      String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
      sendEmail(link, email);

      model.addAttribute("message", "We have sent a reset password link to your email."
          + " Check your Inbox or Spam section.");
    } catch (CustomerNotFoundException e) {
      model.addAttribute("error", e.getMessage());
    } catch (UnsupportedEncodingException | MessagingException e) {
      model.addAttribute("error", "Could not send email.");
    }

    return "customer/forgot_password_form";

  }

  private void sendEmail(String link, String email) throws UnsupportedEncodingException, MessagingException {
    EmailSettingBag emailSettings = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

    String toAddress = email;
    String subject = "Reset password link";
    String content = "<p>Hello, </p>" + "<p>You have request to reset your password.</p>"
        + "<p>Click the link to change your password:</p>"
        + "<p><a href=\"" + link + "\">Change my password </a> </p>"
        + "<br>"
        + "<p>Ignore this email if you remember your password, "
        + "or you have not made the request. </p>";

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
    helper.setTo(toAddress);
    helper.setSubject(subject);

    helper.setText(content, true);
    mailSender.send(message);

  }

  @GetMapping("/reset_password")
  public String showResetForm(@Param("token") String token, Model model) {

    Customer customer = customerService.getByResetPasswordToken(token);
    if (customer != null) {
      model.addAttribute("token", token);
    } else {
      model.addAttribute("pageTitle", "Invalid Token");
      model.addAttribute("message", "Invalid Token");
      return "message";
    }

    return "customer/reset_password_form";

  }

  @PostMapping("/reset_password")
  public String processResetPasswordForm(HttpServletRequest request, Model model) {
    String token = request.getParameter("token");
    String password = request.getParameter("password");

    try {
      customerService.updatePassword(token, password);
      model.addAttribute("pageTitle", "Reset Password Action");
      model.addAttribute("title", "Reset Password Action");
      model.addAttribute("message", "You have successfully change your password !");

      return "message";
    } catch (CustomerNotFoundException e) {
      model.addAttribute("pageTitle", "Invalid Token");
      model.addAttribute("message", e.getMessage());
      return "message";
    }

  }

}
