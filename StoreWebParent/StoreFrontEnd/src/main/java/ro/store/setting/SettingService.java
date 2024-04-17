package ro.store.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.store.common.entity.Currency;
import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

@Service
public class SettingService {

  @Autowired
  private SettingRepository settingRepository;
  @Autowired
  private CurrencyRepository currencyRepository;

  public List<Setting> getGeneralSettings() {
    return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
  }

  public EmailSettingBag getEmailSettings() {
    List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));

    return new EmailSettingBag(settings);
  }

  public CurrencySettingBag getCurrencySettings() {
    List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
    return new CurrencySettingBag(settings);
  }

  public PaymentSettingBag getPaymentSettings() {
    List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
    return new PaymentSettingBag(settings);
  }

  public String getCurrencyCode(){
    Setting setting = settingRepository.findByKey("CURRENCY_ID");
    Integer currencyId = Integer.parseInt(setting.getValue());
    Currency currency = currencyRepository.findById(currencyId).get();
    return currency.getCode();
  }

}
