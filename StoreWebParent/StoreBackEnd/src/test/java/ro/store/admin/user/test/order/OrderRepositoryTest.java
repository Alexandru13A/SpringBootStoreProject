package ro.store.admin.user.test.order;


import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.order.OrderRepository;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.entity.Product.Product;
import ro.store.common.entity.order.Order;
import ro.store.common.entity.order.OrderDetail;
import ro.store.common.entity.order.OrderStatus;
import ro.store.common.entity.order.PaymentMethod;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {
  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TestEntityManager entityManager;



  @Test
  public void testAddNewOrderWithSingleProduct(){

    Customer customer = entityManager.find(Customer.class, 14);

    Product product = entityManager.find(Product.class, 16);

    Order mainOrder = new Order();
    mainOrder.setOrderTime(new Date());
    mainOrder.setCustomer(customer);
    mainOrder.setFirstName(customer.getFirstName());
    mainOrder.setLastName(customer.getLastName());
    mainOrder.setPhoneNumber(customer.getPhoneNumber());
    mainOrder.setAddress1(customer.getAddressLine1());
    if(customer.getAddressLine2() != null){
      mainOrder.setAddress2(customer.getAddressLine2());
    }

    mainOrder.setCity(customer.getCity());
    mainOrder.setState(customer.getState());
    mainOrder.setCountry(customer.getCountry().getName());
    mainOrder.setPostalCode(customer.getPostalCode());


    mainOrder.setShippingCost(10);
    mainOrder.setProductCost(product.getCost());
    mainOrder.setTax(0);
    mainOrder.setSubtotal(product.getPrice());
    mainOrder.setTotal(product.getPrice() + 10);

    mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    mainOrder.setOrderStatus(OrderStatus.NEW);
    mainOrder.setDeliverDate(new Date());
    mainOrder.setDeliverDays(1);

    OrderDetail orderDetail = new OrderDetail();

    orderDetail.setProduct(product);
    orderDetail.setOrder(mainOrder);
    orderDetail.setProductCost(product.getCost());
    orderDetail.setShippingCost(10);
    orderDetail.setQuantity(1);
    orderDetail.setSubtotal(product.getPrice());
    orderDetail.setUnitPrice(product.getPrice());

    mainOrder.getOrderDetails().add(orderDetail);

    Order saveOrder = orderRepository.save(mainOrder);

    assertThat(saveOrder.getId()).isGreaterThan(0);



  }

  

}
