package ro.store.utility;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.setting.EmailSettingBag;

public class Utility {

  public static String getSiteURL(HttpServletRequest request){

    String siteURL = request.getRequestURL().toString();
    
    return siteURL.replace(request.getServletPath(), "");

  }

  public static JavaMailSenderImpl prepareMailSender(EmailSettingBag setting){
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
  
}