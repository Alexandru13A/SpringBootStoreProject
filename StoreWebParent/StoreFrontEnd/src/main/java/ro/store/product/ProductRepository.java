package ro.store.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

  @Query("SELECT p FROM Product p WHERE p.enabled= true" 
  +" AND (p.category.id = ?1 OR p.category.allParentsIds LIKE %?2%)"
  +" ORDER BY p.name ASC")
  public Page<Product> listByCategory(Integer categoryId,String categoryIdMatch,Pageable pageable);

  public Product findByAlias(String alias);

  @Query(value = "SELECT * FROM products WHERE enabled = true AND MATCH(name,short_description, full_description) AGAINST(?1) ", nativeQuery = true)
  public Page<Product> searchProduct(String keyword, Pageable pageable);

}
