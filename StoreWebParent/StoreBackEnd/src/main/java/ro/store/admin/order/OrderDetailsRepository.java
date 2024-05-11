package ro.store.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ro.store.common.entity.order.OrderDetail;

@Repository
public interface OrderDetailsRepository extends CrudRepository<OrderDetail, Integer>  {

  @Query("SELECT NEW ro.store.common.entity.order.OrderDetail(d.product.category.name, d.quantity,"
			+ " d.productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

	@Query("SELECT NEW ro.store.common.entity.order.OrderDetail(d.quantity, d.product.name,"
			+ " d.productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);

  
}