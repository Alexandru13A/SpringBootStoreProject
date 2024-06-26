package ro.store.admin.user.test.setting;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.setting.SettingRepository;
import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

  @Autowired
  private SettingRepository repository;

  @Test
  public void testCreateGeneralSettings() {

    Setting siteName = new Setting("SITE_NAME", "Store", SettingCategory.GENERAL);
    Setting siteLogo = new Setting("SITE_LOGO", "Store.png", SettingCategory.GENERAL);
    Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2023 Store Ltd.", SettingCategory.GENERAL);

    repository.saveAll(List.of(siteName,siteLogo, copyright));
    Iterable<Setting> iterable = repository.findAll();
    assertThat(iterable).size().isGreaterThan(0);
  }

  @Test
  public void testCreateCurrencySettings() {

    Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
    Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
    Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
    Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
    Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
    Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

    repository
        .saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType));

  }

  @Test
  public void orderConfirmationMailSettings(){
    Setting orderConfirmationContent = new Setting("ORDER_CONFIRMATION_CONTENT"," ",SettingCategory.MAIL_TEMPLATES);
    Setting orderConfirmationSubject = new Setting("ORDER_CONFIRMATION_SUBJECT"," ",SettingCategory.MAIL_TEMPLATES);

     repository
        .saveAll(List.of(orderConfirmationContent,orderConfirmationSubject));
  }
  @Test
  public void orderPaymentSettings(){
    Setting paypalApiURL = new Setting("PAYPAL_API_BASE_URL"," ",SettingCategory.PAYMENT);
    Setting paypalApiClientId = new Setting("PAYPAL_API_CLIENT_ID"," ",SettingCategory.PAYMENT);
    Setting paypalApiClientSecret = new Setting("PAYPAL_API_CLIENT_SECRET"," ",SettingCategory.PAYMENT);

  repository.saveAll(List.of(paypalApiURL,paypalApiClientId,paypalApiClientSecret));
  }

  @Test
  public void testListSettingsByCategory() {
    List<Setting> settings = repository.findByCategory(SettingCategory.GENERAL);
    settings.forEach(System.out :: println);

  }

}
