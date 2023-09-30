package ro.store.admin.customer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.customer.CustomerService;

@RestController
public class CustomerRestController {

  @Autowired
  private CustomerService service;

  @PostMapping("/customers/check_email")
  public String checkForDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
    if (service.checkForUniqueEmail(id, email)) {
      return "OK";
    } else {
      return "Duplicated";
    }
  }
}
