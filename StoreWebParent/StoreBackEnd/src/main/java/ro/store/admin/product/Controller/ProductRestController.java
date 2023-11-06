package ro.store.admin.product.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.product.ProductService;

@RestController
public class ProductRestController {

  private final ProductService service;

  public ProductRestController(ProductService service) {
    this.service = service;
  }

  @PostMapping("/products/check_unique")
  public String checkForUniqueName( @RequestParam("id") Integer id,@RequestParam("name") String name) {
    return service.checkForUniqueName(id, name);
  }

}
