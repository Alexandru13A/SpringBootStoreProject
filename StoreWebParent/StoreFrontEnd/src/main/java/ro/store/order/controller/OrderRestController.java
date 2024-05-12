package ro.store.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.common.entity.customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;
import ro.store.common.exception.order.OrderNotFoundException;
import ro.store.customer.CustomerService;
import ro.store.order.OrderReturnRequest;
import ro.store.order.OrderReturnResponse;
import ro.store.order.OrderService;
import ro.store.utility.Utility;

@RestController
public class OrderRestController {

  private OrderService orderService;
  private CustomerService customerService;

  public OrderRestController(OrderService orderService, CustomerService customerService) {
    this.orderService = orderService;
    this.customerService = customerService;
  }

  //HANDEL RETURN REQUEST METHOD
  @PostMapping("/orders/return")
  public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
      HttpServletRequest servletRequest) {

    System.out.println("Order ID: " + returnRequest.getOrderId());
    System.out.println("Reason: " + returnRequest.getReason());
    System.out.println("Note: " + returnRequest.getNote());

    Customer customer = null;

    try {
      customer = getAuthenticatedCustomer(servletRequest);
    } catch (CustomerNotFoundException e) {
      return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
    }

    try {
      orderService.setOrderReturnRequested(returnRequest, customer);
    } catch (OrderNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
  }


  //GET AUTHENTICATED CUSTOMER
  private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {

    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    if (email == null) {
      throw new CustomerNotFoundException("No authenticated customer");
    }
    return customerService.getCustomerByEmail(email);

  }

}
