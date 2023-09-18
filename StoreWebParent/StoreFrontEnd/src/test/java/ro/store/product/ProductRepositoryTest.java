package ro.store.product;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ro.store.common.entity.Product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {

  @Autowired
  ProductRepository repository;

  @Test
  public void testFindByAlias(){
    String alias = "Asus-TUF-Gaming-A15-Gaming-Laptop";
    Product product= repository.findByAlias(alias);
    assertThat(product).isNotNull();
  }

}
