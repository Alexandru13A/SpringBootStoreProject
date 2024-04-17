package ro.store.setting;

import java.util.List;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingBag;

public class CurrencySettingBag extends SettingBag {

  public CurrencySettingBag(List<Setting> listSettings) {
    super(listSettings);
  }

  public String getCurrencySymbol(){
    return super.getValue("CURRENCY_SYMBOL");
  }
  public String getCurrencySymbolPosition(){
    return super.getValue("CURRENCY_SYMBOL_POSITION");
  }
  public String getCurrencyDecimalPointType(){
    return super.getValue("DECIMAL_POINT_TYPE");
  }
  public String getCurrencyThousandsPointType(){
    return super.getValue("THOUSANDS_POINT_TYPE");
  }
  public int getCurrencyDecimalDigits(){
    return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
  }




  
  
}
