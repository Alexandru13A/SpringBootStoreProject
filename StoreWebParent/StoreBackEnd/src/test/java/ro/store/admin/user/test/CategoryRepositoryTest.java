package ro.store.admin.user.test;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.category.CategoryRepository;
import ro.store.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository repo;

  @Test
  public void testCreateRootCategory() {
    Category category = new Category("Electronics");
    Category savedCategory = repo.save(category);
    assertThat(savedCategory.getId()).isGreaterThan(0);

  }

  @Test
  public void testCreateSubcategory() {
    Category parent = new Category(5);
    Category subCategory = new Category("Gaming Laptops", parent);

    repo.save(subCategory);

  }

  @Test
  public void testGetCategory() {
    Category category = repo.findById(2).get();
    System.out.println(category.getName());

    Set<Category> children = category.getChildren();
    for (Category subCategory : children) {
      System.out.println(subCategory.getName());
    }

    assertThat(children.size()).isGreaterThan(0);
  }

  @Test
  public void testPrintHierarchicalCategories() {
    Iterable<Category> categories = repo.findAll();

    for (Category category : categories) {
      if (category.getParent() == null) {
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
          System.out.println("--" + subCategory.getName());
          printChildren(subCategory, 1);
        }

      }
    }

  }

  private void printChildren(Category parent, int subLevel) {
    int newSubLevel = subLevel + 1;
    Set<Category> children = parent.getChildren();

    for (Category subCategory : children) {
      for (int i = 0; i < newSubLevel; i++) {
        System.out.println("--");
      }
      System.out.println(subCategory.getName());

      printChildren(subCategory, newSubLevel);
    }
  }

  @Test
  public void testListRootCategories() {

    List<Category> rootCategories = repo.findRootCategories();
    rootCategories.forEach(cat -> System.out.println(cat.getName()));

  }

}