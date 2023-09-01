package ro.store.admin.user.test.brand;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.barnd.BrandRepository;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

  @Autowired
  private BrandRepository repository;

  @Test
  public void createBrandTest() {
    Category laptops = new Category(4);
    Brand acer = new Brand();
    acer.setName("Acer");
    acer.setLogo("acer.png");
    acer.getCategories().add(laptops);

    Brand saveBrand = repository.save(acer);

    assertThat(saveBrand).isNotNull();
    assertThat(saveBrand.getId()).isGreaterThan(0);

  }

  @Test
  public void createBrandTest2() {
    Category smartphones = new Category(10);
    Category tablets = new Category(21);

    Brand apple = new Brand();
    apple.setName("Apple");
    apple.setLogo("apple.png");
    apple.getCategories().add(smartphones);
    apple.getCategories().add(tablets);

    Brand savedBrand = repository.save(apple);

    assertThat(savedBrand).isNotNull();
    assertThat(savedBrand.getId()).isGreaterThan(0);

  }

  @Test
  public void createBrandTest3() {

    Brand samsung = new Brand();
    samsung.setName("Samsung");
    samsung.setLogo("samsung.png");

    samsung.getCategories().add(new Category(10));
    samsung.getCategories().add(new Category(21));

    Brand savedBrand = repository.save(samsung);

    assertThat(savedBrand).isNotNull();
    assertThat(savedBrand.getId()).isGreaterThan(0);

  }

  @Test
  public void findAllTest() {

    Iterable<Brand> brands = repository.findAll();
    brands.forEach(System.out::println);
    assertThat(brands).isNotEmpty();

  }

  @Test
  public void getByIdTest() {
    Brand brand = repository.findById(1).get();

    assertThat(brand.getName()).isEqualTo("Acer");
  }

  @Test
  public void updateNameTest() {
    String name = "Samsung Electronics";

    Brand samsung = repository.findById(3).get();
    samsung.setName(name);

    Brand savedBrand = repository.save(samsung);
    assertThat(savedBrand.getName()).isEqualTo(name);
  }

  @Test
  public void deleteTest() {
    Integer id = 3;
    repository.deleteById(id);

    Optional<Brand> result = repository.findById(id);
    assertThat(result.isEmpty());

  }

}
