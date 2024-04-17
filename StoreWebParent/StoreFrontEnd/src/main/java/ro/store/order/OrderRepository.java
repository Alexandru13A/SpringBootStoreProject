package ro.store.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ro.store.common.entity.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {



  
}
