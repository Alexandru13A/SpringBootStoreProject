package ro.store.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.store.common.entity.Setting.GeneralSettingBag;
import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

@Service
public class SettingService {

  @Autowired
  private SettingRepository settingRepository;

  public List<Setting> listAllSettings() {
    return settingRepository.findAll();
  }

  public GeneralSettingBag getGeneralSettings() {
    List<Setting> settings = new ArrayList<>();

    List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
    List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

    settings.addAll(generalSettings);
    settings.addAll(currencySettings);

    return new GeneralSettingBag(settings);
  }

  public void saveAll(Iterable<Setting> settings) {
    settingRepository.saveAll(settings);
  }

}
