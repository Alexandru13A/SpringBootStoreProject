package ro.store.shoppingCart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.CartItem;
import ro.store.common.entity.Product.Product;
import ro.store.common.entity.customer.Customer;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

  public List<CartItem> findByCustomer(Customer customer);

  public CartItem findByCustomerAndProduct(Customer customer, Product product);

  @Query("UPDATE CartItem c SET c.quantity=?1 WHERE c.customer.id = ?2 AND c.product.id=?3")
  @Modifying
  public void updateQuantity(Integer quantity, Integer customerId, Integer productId);

  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.customer.id=?1 AND c.product.id =?2")
  public void deleteByCustomerAndProduct(Integer customerId, Integer productId);

  @Modifying
  @Query("DELETE CartItem c WHERE c.customer.id = ?1")
  public void deleteByCustomer(Integer customerId);
}
