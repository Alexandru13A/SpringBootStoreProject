package ro.store.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ro.store.checkout.CheckoutInfo;
import ro.store.common.entity.Address;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.customer.Customer;
import ro.store.common.entity.order.Order;
import ro.store.common.entity.order.OrderDetail;
import ro.store.common.entity.order.OrderStatus;
import ro.store.common.entity.order.OrderTrack;
import ro.store.common.entity.order.PaymentMethod;
import ro.store.common.entity.product.Product;
import ro.store.common.exception.order.OrderNotFoundException;

@Service
public class OrderService {

  public static final int ORDERS_PER_PAGE = 5;

  private OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }


  //PLACE AN ORDER
  public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
      CheckoutInfo checkoutInfo) {

    Order order = new Order();
    order.setOrderTime(new Date());

    if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
      order.setOrderStatus(OrderStatus.PAID);
    } else {
      order.setOrderStatus(OrderStatus.NEW);
    }

    order.setCustomer(customer);
    order.setProductCost(checkoutInfo.getProductCost());
    order.setSubtotal(checkoutInfo.getProductTotal());
    order.setShippingCost(checkoutInfo.getShippingCostTotal());
    order.setTax(0.0f);
    order.setTotal(checkoutInfo.getPaymentTotalForOrder());
    order.setPaymentMethod(paymentMethod);
    order.setDeliverDays(checkoutInfo.getDeliverDays());
    order.setDeliverDate(checkoutInfo.getDeliverDate());

    if (address == null) {
      order.copyAddressFromCustomer();
    } else {
      order.copyShippingAddress(address);
    }

    Set<OrderDetail> orderDetails = order.getOrderDetails();

    for (CartItem cartItem : cartItems) {
      Product product = cartItem.getProduct();

      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setQuantity(cartItem.getQuantity());
      orderDetail.setUnitPrice(product.getDiscountPrice());
      orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
      orderDetail.setSubtotal(cartItem.getSubtotal());
      orderDetail.setShippingCost(cartItem.getShippingCost());

      orderDetails.add(orderDetail);

    }

    OrderTrack track = new OrderTrack();
    track.setOrder(order);
    track.setStatus(OrderStatus.NEW);
    track.setNotes(OrderStatus.NEW.defaultDescription());
    track.setUpdatedTime(new Date());

    order.getOrderTracks().add(track);

    return orderRepository.save(order);

  }

  //FRONT END CUSTOMER ORDERS
  public Page<Order> listOrdersForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir,
      String keyword) {

    Sort sort = Sort.by(sortField);
    sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

    Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

    if (keyword != null) {
      orderRepository.findAll(keyword, customer.getId(), pageable);
    }

    return orderRepository.findAll(customer.getId(), pageable);

  }

  //GET ORDER FROM DB
  public Order getOrder(Integer id, Customer customer) {
    return orderRepository.findByIdAndCustomer(id, customer);
  }

  //SET ORDER RETURN REQUEST
  public void setOrderReturnRequested(OrderReturnRequest request, Customer customer)
      throws OrderNotFoundException {

    Order order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);

    if (order == null) {
      throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not found");
    }

    if (order.isReturnRequested())
      return;

    OrderTrack orderTrack = new OrderTrack();

    orderTrack.setOrder(order);
    orderTrack.setUpdatedTime(new Date());
    orderTrack.setStatus(OrderStatus.RETURN_REQUESTED);

    String notes = "Reason: " + request.getReason();
    if (!"".equals(request.getNote())) {
      notes += ". " + request.getNote();
    }

    orderTrack.setNotes(notes);

    order.getOrderTracks().add(orderTrack);
    order.setOrderStatus(OrderStatus.RETURN_REQUESTED);

    orderRepository.save(order);

  }
}
