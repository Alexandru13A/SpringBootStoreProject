package ro.store.common.entity.Setting;

import java.util.List;

public class GeneralSettingBag extends SettingBag {

  public GeneralSettingBag(List<Setting> lisSettings) {
    super(lisSettings);
  }

  public void updateCurrencySymbol(String value){
    super.update("CURRENCY_SYMBOL", value);
  }

  public void updateSiteLogo(String value){
    super.update("SITE_LOGO", value);
  }

}
