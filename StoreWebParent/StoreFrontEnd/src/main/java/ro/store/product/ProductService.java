package ro.store.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ro.store.common.entity.product.Product;
import ro.store.common.exception.product.ProductNotFoundException;

@Service
public class ProductService {
  public static final int PRODUCT_PER_PAGE_FRONTEND = 10;
  public static final int SEARCH_RESULT_PER_PAGE = 10;

  @Autowired
  private ProductRepository productRepository;

  public Page<Product> listByCategory(int pageNum, Integer categoryId) {

    String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
    PageRequest pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE_FRONTEND);

    return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
  }

  public Product getProductByAlias(String alias) throws ProductNotFoundException {
    Product product = productRepository.findByAlias(alias);
    if (product == null) {
      throw new ProductNotFoundException("Could not find any product with alias " + alias);
    }

    return product;
  }

  public Page<Product> searchProduct(String keyword, int pageNum) {
    PageRequest pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
    return productRepository.searchProduct(keyword, pageable);

  }
}
