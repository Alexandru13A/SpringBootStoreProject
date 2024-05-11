package ro.store.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.common.entity.customer.Customer;
import ro.store.common.entity.order.Order;
import ro.store.customer.CustomerService;
import ro.store.order.OrderService;
import ro.store.utility.Utility;

@Controller
public class OrderController {

  private CustomerService customerService;
  private OrderService orderService;

  public OrderController(CustomerService customerService, OrderService orderService) {
    this.customerService = customerService;
    this.orderService = orderService;
  }

  @GetMapping("/orders")
  public String getOrders(Model model, HttpServletRequest request) {
    return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
  }

  @GetMapping("/orders/page/{pageNum}")
  public String listOrdersByPage(Model model, HttpServletRequest request, @PathVariable("pageNum") int pageNum,
      String sortField, String sortDir, String keyword) {

        Customer customer = getAuthenticatedCustomer(request);

        Page<Order> page = orderService.listOrdersForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);

        List<Order> orders = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("orders", orders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("moduleURL", "/orders");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
          endCount = page.getTotalElements();
        }
        model.addAttribute("endCount", endCount);

    return "orders/orders_customer";
  }

  public Customer getAuthenticatedCustomer(HttpServletRequest request) {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    return customerService.getCustomerByEmail(email);
  }

}
