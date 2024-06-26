package ro.store.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.security.oauth2.CustomerOAuth2User;
import ro.store.setting.CurrencySettingBag;
import ro.store.setting.EmailSettingBag;

public class Utility {

  public static String getSiteURL(HttpServletRequest request) {

    String siteURL = request.getRequestURL().toString();

    return siteURL.replace(request.getServletPath(), "");

  }

  public static JavaMailSenderImpl prepareMailSender(EmailSettingBag setting) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost(setting.getHost());
    mailSender.setPort(setting.getPort());
    mailSender.setUsername(setting.getUsername());
    mailSender.setPassword(setting.getPassword());

    Properties mailProperties = new Properties();
    mailProperties.setProperty("mail.smtp.auth", setting.getSmtpAuth());
    mailProperties.setProperty("mail.smtp.starttls.enable", setting.getSmtpSecured());

    mailSender.setJavaMailProperties(mailProperties);

    return mailSender;
  }

  public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
    Object principal = request.getUserPrincipal();
    if (principal == null)
      return null;

    String customerEmail = null;

    if (principal instanceof UsernamePasswordAuthenticationToken
        || principal instanceof RememberMeAuthenticationToken) {
      customerEmail = request.getUserPrincipal().getName();

    } else if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;

      CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
      customerEmail = oAuth2User.getEmail();
    }

    return customerEmail;
  }

  public static String formatCurrency(float amount, CurrencySettingBag currencySettingBag) {

    String symbol = currencySettingBag.getCurrencySymbol();
    String symbolPosition = currencySettingBag.getCurrencySymbolPosition();
    String decimalPointType = currencySettingBag.getCurrencyDecimalPointType();
    String thousandsPointType = currencySettingBag.getCurrencyThousandsPointType();
    int decimalDigits = currencySettingBag.getCurrencyDecimalDigits();

    String pattern = symbolPosition.equals("Before price") ? symbol : "";
    pattern += "###,###";

    if (decimalDigits > 0) {
      pattern += ".";
      for (int count = 1; count <= decimalDigits; count++)
        pattern += "#";
    }

    pattern += symbolPosition.equals("After price") ? symbol : "";

    char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';
    char thousandsSeparator = thousandsPointType.equals("POINT") ? '.' : ',';

    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
    decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
    decimalFormatSymbols.setGroupingSeparator(thousandsSeparator);

    DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);

    return formatter.format(amount);
  }

}