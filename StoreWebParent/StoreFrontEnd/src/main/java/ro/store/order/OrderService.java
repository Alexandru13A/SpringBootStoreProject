package ro.store.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ro.store.checkout.CheckoutInfo;
import ro.store.common.entity.Address;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.entity.Product.Product;
import ro.store.common.entity.order.Order;
import ro.store.common.entity.order.OrderDetail;
import ro.store.common.entity.order.OrderStatus;
import ro.store.common.entity.order.PaymentMethod;

@Service
public class OrderService {

  private OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
      CheckoutInfo checkoutInfo) {

    Order order = new Order();
    order.setOrderTime(new Date());
    order.setOrderStatus(OrderStatus.NEW);
    order.setCustomer(customer);
    order.setProductCost(checkoutInfo.getProductCost());
    order.setSubtotal(checkoutInfo.getProductTotal());
    order.setShippingCost(checkoutInfo.getShippingCostTotal());
    order.setTax(0.0f);
    order.setTotal(checkoutInfo.getPaymentTotalForOrder());
    order.setPaymentMethod(paymentMethod);
    order.setDeliverDays(checkoutInfo.getDeliverDays());
    order.setDeliverDate(checkoutInfo.getDeliverDate());
    
    if(address == null){
      order.copyAddressFromCustomer();
    }else{
      order.copyShippingAddress(address);
    }

    Set<OrderDetail> orderDetails = order.getOrderDetails();

    for(CartItem cartItem : cartItems){
      Product product = cartItem.getProduct();

      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setQuantity(cartItem.getQuantity());
      orderDetail.setUnitPrice(product.getDiscountPrice());
      orderDetail.setProductCost(product.getCost()* cartItem.getQuantity());
      orderDetail.setSubtotal(cartItem.getSubtotal());
      orderDetail.setShippingCost(cartItem.getShippingCost());

      orderDetails.add(orderDetail);

    }


    return orderRepository.save(order);

  }

}
