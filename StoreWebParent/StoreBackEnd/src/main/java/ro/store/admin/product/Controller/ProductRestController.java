package ro.store.admin.product.Controller;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.product.ProductDTO;
import ro.store.admin.product.ProductService;
import ro.store.common.entity.product.Product;
import ro.store.common.exception.product.ProductNotFoundException;

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

  @GetMapping("/products/get/{id}")
	public ProductDTO getProductInfo(@PathVariable("id") Integer id) 
			throws ProductNotFoundException {
		Product product = service.getProductByID(id);
		return new ProductDTO(product.getName(), product.getMainImagePath(), 
				product.getDiscountPrice(), product.getCost());
	}

}
