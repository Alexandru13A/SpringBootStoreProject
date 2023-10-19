package ro.store.admin.customer.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.store.admin.customer.CustomerService;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.exception.customer.CustomerNotFoundException;

@Controller
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @GetMapping("/customers")
  public String listFirstPage(Model model) {
    return listByPage(model, 1, "firstName", "asc", null);
  }

  @GetMapping("/customers/page/{pageNum}")
  public String listByPage(Model model,
      @PathVariable(name = "pageNum") int pageNum,
      @Param("sortField") String sortField,
      @Param("sortOrder") String sortOrder,
      @Param("keyword") String keyword) {

    Page<Customer> page = customerService.listByPage(pageNum, sortField, sortOrder, keyword);
    List<Customer> customers = page.getContent();

    long startCount = (pageNum - 1) * CustomerService.CUSTOMER_PER_PAGE + 1;
    model.addAttribute("startCount", startCount);

    long endCount = startCount + CustomerService.CUSTOMER_PER_PAGE - 1;
    if (endCount > page.getTotalElements()) {
      endCount = page.getTotalElements();
    }

    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("currentPage", pageNum);
    model.addAttribute("customers", customers);
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");
    model.addAttribute("endCount", endCount);

    return "customers/customers";

  }

  @GetMapping("/customers/{id}/enabled/{status}")
  public String updateCustomerEnableStatus(@PathVariable("id") Integer id,
      @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

    customerService.updateStatus(id, enabled);
    String status = enabled ? "enabled" : "disabled";
    String message = "The customer with ID: " + id + " has been " + status;
    redirectAttributes.addFlashAttribute("message", message);

    return "redirect:/customers";
  }

  @GetMapping("/customers/detail/{id}")
  public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Customer customer = customerService.get(id);
      
      model.addAttribute("customer", customer);
      model.addAttribute("createdTime", customer.getCreatedTime());

      return "customers/customer_detail_modal";
    } catch (CustomerNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return "redirect:/customers";
    }
  }

  @GetMapping("/customers/edit/{id}")
  public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Customer customer = customerService.get(id);
      customer.setCreatedTime(customer.getCreatedTime());
      List<Country> countries = customerService.listAllCountries();

      model.addAttribute("customer", customer);
      model.addAttribute("countries", countries);
      model.addAttribute("pageTitle", String.format("Edit customer (ID: %d)", id));
      return "customers/customer_form";

    } catch (CustomerNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return " redirect:/customers";
    }
  }

  @PostMapping("/customers/save")
  public String saveCustomer(Customer customer, Model model, RedirectAttributes redirectAttributes) {
    customerService.saveCustomer(customer);
    redirectAttributes.addFlashAttribute("message", "The customer with ID " + customer.getId() + " has been updated.");
    return "redirect:/customers";
  }

  @GetMapping("/customers/delete/{id}")
  public String deleteCustomer(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

    try {
      customerService.deleteCustomer(id);
      redirectAttributes.addFlashAttribute("message", "The customer with ID " + id + " has been deleted.");
    } catch (CustomerNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/customers";

  }

}
