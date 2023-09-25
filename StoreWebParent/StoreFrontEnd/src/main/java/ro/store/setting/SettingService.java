package ro.store.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

@Service
public class SettingService {

  @Autowired
  private SettingRepository settingRepository;


  public List<Setting> getGeneralSettings() {
    return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
  }

  

}
