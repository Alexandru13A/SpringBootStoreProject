package ro.store.category;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ro.store.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository repository;

  @Test
  public void testListEnabledCategories() {
    List<Category> categories = repository.findAllEnabled();
    categories.forEach(category -> {
      System.out.println(category.getName() + " Category enabled: " + category.isEnabled());
    });
  }

  @Test
  public void testCategoryByAlias(){
    String value = "electronics";
    Category category = repository.findByAliasEnabled(value);
    assertThat(category).isNotNull();
  }

}
