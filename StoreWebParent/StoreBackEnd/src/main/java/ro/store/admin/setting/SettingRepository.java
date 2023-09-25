package ro.store.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting,String> {

  public List<Setting> findByCategory(SettingCategory category);
  
}
