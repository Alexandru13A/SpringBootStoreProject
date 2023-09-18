package ro.store.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.common.entity.Product.Product;
import ro.store.common.exception.product.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
  @Autowired
  private ProductRepository repository;

  public static final int PRODUCT_PER_PAGE = 5;

  public List<Product> productsList() {
    return repository.findAll();
  }

  public Page<Product> listProductByPage(int pageNum, String sortField, String sortOrder, String keyword,
      Integer categoryId) {

    Sort sort = Sort.by(sortField);
    sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

    PageRequest pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);

    if (keyword != null && !keyword.isEmpty()) {
      if (categoryId != null && categoryId > 0) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        return repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
      }
      return repository.findAll(keyword, pageable);
    }

    if (categoryId != null && categoryId > 0) {
      String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
      return repository.findAllInCategory(categoryId, categoryIdMatch, pageable);
    }
    return repository.findAll(pageable);

  }

  public Product saveProduct(Product product) {
    if (product.getId() == null) {
      product.setTimeWhenIsCreated(new Date());
    }
    if (product.getAlias() == null || product.getAlias().isEmpty()) {
      String defaultAlias = product.getName().replace(" ", "-");
      product.setAlias(defaultAlias);
    } else {
      product.setAlias(product.getAlias().replace(" ", "-"));
    }
    product.setTimeWhenIsUpdated(new Date());
    return repository.save(product);

  }

  public void saveProductPrice(Product productInForm) {
    Product product = repository.findById(productInForm.getId()).get();
    product.setCost(productInForm.getCost());
    product.setPrice(productInForm.getPrice());
    product.setDiscount(productInForm.getDiscount());

    repository.save(product);

  }

  public String checkForUniqueName(Integer id, String name) {

    boolean isCreatingNewProduct = (id == null || id == 0);
    Product getProductByName = repository.findByName(name);

    if (isCreatingNewProduct) {
      if (getProductByName != null)
        return "Duplicate";
    } else {
      if (getProductByName != null && getProductByName.getId() != id) {
        return "Duplicate";
      }

    }
    return "OK";

  }

  public Product getProductByID(Integer id) throws ProductNotFoundException {

    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new ProductNotFoundException("No such element found with ID: " + id);
    }
  }

  public void deleteProduct(Integer id) throws ProductNotFoundException {
    Long countById = repository.countById(id);

    if (countById == null || countById == 0) {
      throw new ProductNotFoundException("Could not found any product with ID: " + id);
    }
    repository.deleteById(id);
  }

  public void updateStatus(Integer id, boolean enabled) {
    repository.updateProductStatus(id, enabled);
  }

}
