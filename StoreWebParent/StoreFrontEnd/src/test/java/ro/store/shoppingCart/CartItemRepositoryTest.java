package ro.store.shoppingCart;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.store.common.entity.CartItem;
import ro.store.common.entity.customer.Customer;
import ro.store.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {


  private CartItemRepository cartItemRepository;
  private TestEntityManager entityManager;


  public CartItemRepositoryTest(CartItemRepository cartItemRepository, TestEntityManager entityManager) {
    this.cartItemRepository = cartItemRepository;
    this.entityManager = entityManager;
  }


  @Test
  public void testSaveItem() {

    Integer customerId = 15;
    Integer productId = 17;

    Customer customer = entityManager.find(Customer.class, customerId);
    Product product = entityManager.find(Product.class, productId);

    CartItem cartItem = new CartItem();
    cartItem.setCustomer(customer);
    cartItem.setProduct(product);
    cartItem.setQuantity(1);

    CartItem savedItem = cartItemRepository.save(cartItem);

    assertThat(savedItem.getId()).isGreaterThan(0);

  }

  @Test
  public void testSave2Items() {

    Integer customerId = 15;
    Integer productId = 17;

    Customer customer = entityManager.find(Customer.class, customerId);
    Product product = entityManager.find(Product.class, productId);

    CartItem item1 = new CartItem();
    item1.setCustomer(customer);
    item1.setProduct(product);
    item1.setQuantity(2);

    CartItem item2 = new CartItem();
    item2.setCustomer(new Customer(customerId));
    item2.setProduct(new Product(18));
    item2.setQuantity(3);

    Iterable<CartItem> iterable = cartItemRepository.saveAll(List.of(item1, item2));

    assertThat(iterable).size().isGreaterThan(0);
  }

  @Test
  public void testFindByCustomer() {
    Integer customerId = 15;
    List<CartItem> items = cartItemRepository.findByCustomer(new Customer(customerId));

    items.forEach(System.out::println);

    assertThat(items.size()).isEqualTo(3);
  }

  @Test
  public void testFindByCustomerAndProduct(){
    Integer customerId = 15;
    Integer productId = 17;

   CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

   assertThat(cartItem).isNotNull();
   System.out.println(cartItem);
  }

  @Test
  public void testUpdateQuantity(){

    Integer customerId = 15;
    Integer productId = 17;
    Integer quantity = 4;
    cartItemRepository.updateQuantity(quantity, customerId, productId);
    CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

    assertThat(item.getQuantity()).isEqualTo(4);

  }

  @Test
  public void testDeleteByCustomerAndProduct(){
    Integer customerId = 15;
    Integer productId = 16;

    cartItemRepository.deleteByCustomerAndProduct(customerId, productId);

     CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

     assertThat(item).isNull();

  }

}
