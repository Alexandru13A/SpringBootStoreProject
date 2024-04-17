package ro.store.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import ro.store.admin.common.paging.SearchRepository;
import ro.store.common.entity.Brand;

public interface BrandRepository extends SearchRepository<Brand, Integer> {

  public Long countById(Integer id);

  public Brand findByName(String name);

  @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
  Page<Brand> findAll(String keyword, Pageable pageable);

  @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
	public List<Brand> findAll();
  

}
