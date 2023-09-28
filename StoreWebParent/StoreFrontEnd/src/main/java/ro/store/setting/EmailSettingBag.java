package ro.store.setting;

import java.util.List;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingBag;

public class EmailSettingBag extends SettingBag {

  public EmailSettingBag(List<Setting> listSetting) {
    super(listSetting);
  }

  public String getHost(){
    return super.getvalue("MAIL_HOST");
  }

  public Integer getPort(){
    return Integer.parseInt(super.getvalue("MAIL_PORT"));
  }
  
  public String getUsername(){
    return super.getvalue("MAIL_USERNAME");
  }
  
  public String getPassword(){
    return super.getvalue("MAIL_PASSWORD");
  }
  
  public String getSmtpAuth(){
    return super.getvalue("SMTP_AUTH");
  }
  
  public String getSmtpSecured(){
    return super.getvalue("SMTP_SECURED");
  }
  
  public String getFromAddress(){
    return super.getvalue("MAIL_FROM");
  }
  
  public String getSenderName(){
    return super.getvalue("MAIL_SENDER_NAME");
  }
  
  public String getCustomerVerifySubject(){
    return super.getvalue("CUSTOMER_VERIFY_SUBJECT");
  }
  
  public String getCustomerVerifyContent(){
    return super.getvalue("CUSTOMER_VERIFY_CONTENT");
  }
  

}
