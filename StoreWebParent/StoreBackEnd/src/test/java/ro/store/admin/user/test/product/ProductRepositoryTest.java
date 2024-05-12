package ro.store.admin.user.test.product;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.product.ProductRepository;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;
import ro.store.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository repository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testCreateProduct() {
    Brand brand = entityManager.find(Brand.class, 6);
    Category category = entityManager.find(Category.class, 14);

    Product product = new Product();
    product.setName("Acer Nitro 5");
    product.setAlias("acer_nitro_5");
    product.setShortDescription("Laptop Gaming Acer Nitro 5 AN515-57");
    product.setFullDescription("Laptop Gaming Acer Nitro 5 AN515-57 cu procesor Intel\u00AE Core\u2122 i5-11400H");

    product.setBrand(brand);
    product.setCategory(category);

    product.setPrice(678);
    product.setTimeWhenIsCreated(new Date());
    product.setTimeWhenIsUpdated(new Date());

    Product savedProduct = repository.save(product);

    assertThat(savedProduct).isNotNull();
    assertThat(savedProduct.getId()).isGreaterThan(0);

  }

  @Test
  public void updateProduct() {
    Product product1 = repository.findById(1).get();
    product1.setMainImage("image1/png");
    repository.save(product1);
    Product product2 = repository.findById(2).get();
    product2.setMainImage("image1/png");
    repository.save(product2);
    Product product3 = repository.findById(3).get();
    product3.setMainImage("image1/png");
    repository.save(product3);
    Product product4 = repository.findById(7).get();
    product4.setMainImage("image1/png");
    repository.save(product4);

  }

  @Test 
  public void testSaveProductWithDetails(){
    Integer productId= 12;
    Product product = repository.findById(productId).get();

    product.addDetails("1", "1");
    product.addDetails("2", "2");
    product.addDetails("3", "3");

    Product saveProduct = repository.save(product);
    assertThat(saveProduct.getDetails()).isNotEmpty();
  }

}
