package ro.store.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.common.entity.Product.Product;

@Service
@Transactional
public class ProductService {
  @Autowired
  private ProductRepository productRepo;

  public List<Product> productsList() {
    return productRepo.findAll();
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
    return productRepo.save(product);

  }


  public String checkForUniqueName(Integer id, String name) {

    boolean isCreatingNewProduct = (id == null || id == 0);
    Product getProductByName = productRepo.findByName(name);

    if(isCreatingNewProduct){
      if(getProductByName != null  ) return "Duplicate";
    }else{
      if(getProductByName != null && getProductByName.getId() != id){
        return "Duplicate";
      }

    }
    return "OK";
 
  }



  public Product getProductByID(Integer id) throws ProductNotFoundException {

    try {
      return productRepo.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new ProductNotFoundException("No such element found with ID: " + id);
    }
  }

  public void deleteProduct(Integer id) throws ProductNotFoundException {
    Long countById = productRepo.countById(id);

    if (countById == null || countById == 0) {
      throw new ProductNotFoundException("Could not found any product with ID: " + id);
    }
    productRepo.deleteById(id);
  }

  public void updateStatus(Integer id, boolean enabled) {
    productRepo.updateProductStatus(id, enabled);
  }

  

}
