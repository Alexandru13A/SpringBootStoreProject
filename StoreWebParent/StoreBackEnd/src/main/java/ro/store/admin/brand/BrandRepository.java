package ro.store.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

  public Long countById(Integer id);

  public Brand findByName(String name);

  @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
  Page<Brand> search(String keyword, Pageable pageable);

}
