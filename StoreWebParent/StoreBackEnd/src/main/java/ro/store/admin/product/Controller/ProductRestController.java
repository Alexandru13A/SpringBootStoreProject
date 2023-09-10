package ro.store.admin.product.Controller;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.product.ProductService;

@RestController
public class ProductRestController {

  private final ProductService service;

  public ProductRestController(ProductService service) {
    this.service = service;
  }

  @PostMapping("/products/check_unique")
  public String checkForUniqueName( @Param("id") Integer id,@Param("name") String name) {
    return service.checkForUniqueName(id, name);
  }

}
