package ro.store.admin.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.order.Order;


public interface OrderRepository extends JpaRepository<Order, Integer> {

  @Query("SELECT o FROM Order o WHERE o.firstName LIKE %?1% OR"
      + " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
      + " o.address1 LIKE %?1% OR o.address2 LIKE %?1% OR"
      + " o.postalCode LIKE %?1% OR o.city LIKE %?1% OR"
      + " o.state LIKE %?1% OR o.country LIKE %?1% OR"
      + " o.paymentMethod LIKE %?1% OR o.orderStatus LIKE %?1% OR"
      + " o.customer.firstName LIKE %?1% OR"
      + " o.customer.lastName LIKE %?1%")
  public Page<Order> findAll(String keyword, Pageable pageable);

  public Long countById(Integer id);

}
