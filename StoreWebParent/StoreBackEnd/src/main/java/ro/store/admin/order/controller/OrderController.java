package ro.store.admin.order.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.order.OrderNotFoundException;
import ro.store.admin.order.OrderService;
import ro.store.admin.setting.SettingService;
import ro.store.common.entity.Country;
import ro.store.common.entity.Setting.Setting;
import ro.store.common.entity.order.Order;

@Controller
public class OrderController {

  private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=asc";
  private OrderService orderService;
  private SettingService settingService;

  public OrderController(OrderService orderService, SettingService settingService) {
    this.orderService = orderService;
    this.settingService = settingService;

  }

  @GetMapping("/orders")
  public String listFirstPage() {
    return defaultRedirectURL;
  }

  @GetMapping("/orders/page/{pageNum}")
  public String listByPage(
      @PagingAndSortingParam(listName = "orders", moduleURL = "/orders") PagingAndSortingHelper helper,
      @PathVariable(name = "pageNum") int pageNum, HttpServletRequest request) {

    orderService.listByPage(pageNum, helper);
    loadCurrencySetting(request);

    return "orders/orders";
  }

  private void loadCurrencySetting(HttpServletRequest request) {
    List<Setting> currencySettings = settingService.getCurrencySettings();

    for (Setting setting : currencySettings) {
      request.setAttribute(setting.getKey(), setting.getValue());
    }
  }

  @GetMapping("/orders/detail/{id}")
  public String orderDetail(@PathVariable("id") Integer id, Model model, HttpServletRequest request,
      RedirectAttributes redirectAttributes)
      throws OrderNotFoundException {

    try {
      Order order = orderService.getOrderById(id);
      loadCurrencySetting(request);
      model.addAttribute("order", order);
      model.addAttribute("pageTitle", "Order Details");
      return "orders/order_details_modal";
    } catch (OrderNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return defaultRedirectURL;
    }
  }

  @GetMapping("/orders/delete/{id}")
  public String deleteOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

    try {
      orderService.deleteOrderById(id);
      redirectAttributes.addFlashAttribute("message", "Order with ID: " + id + " has been deleted.");
    } catch (OrderNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }

    return defaultRedirectURL;
  }

  @GetMapping("/orders/edit/{id}")
  public String editOrder(
      @PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
      HttpServletRequest request) {


        try{
          Order order = orderService.getOrderById(id);
          List<Country> countries = orderService.listAllCountries();

          model.addAttribute("pageTitle", "Edit order "+id);
          model.addAttribute("order", order);
          model.addAttribute("countries", countries);

          return "orders/order_form";
        }catch(OrderNotFoundException e){
          redirectAttributes.addFlashAttribute("message",e.getMessage());
          return defaultRedirectURL;
        }

  }

}
