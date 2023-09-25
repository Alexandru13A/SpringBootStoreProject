package ro.store.setting;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.Setting.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

  @Autowired
  private SettingRepository settingRepository;

  @Test
  public void testFindByTwoCategories() {

    List<Setting> settings = settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    settings.forEach(System.out::println);

  }

}
