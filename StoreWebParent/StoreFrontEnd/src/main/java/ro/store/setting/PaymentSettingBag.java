package ro.store.setting;

import java.util.List;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingBag;

public class PaymentSettingBag extends SettingBag {

  public PaymentSettingBag(List<Setting> listSetting) {
    super(listSetting);

  }

  public String getURL() {
    return super.getValue("PAYPAL_API_BASE_URL");
  }

  public String getClientID() {
    return super.getValue("PAYPAL_API_CLIENT_ID");
  }

  public String getClientSecret() {
    return super.getValue("PAYPAL_API_CLEINT_SECRET");
  }

}
