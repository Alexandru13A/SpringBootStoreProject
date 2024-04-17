package ro.store.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting, String> {

  public List<Setting> findByCategory(SettingCategory category);
  
  @Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
  public List<Setting> findByTwoCategories(SettingCategory categoryOne,SettingCategory categoryTwo);

  public Setting findByKey(String key);
}
