package ro.store.admin.barnd;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Transient;
import org.springframework.stereotype.Service;

import ro.store.common.entity.Brand;

@Service
@Transient
public class BrandService {

  public static final int BRAND_PER_PAGE = 6;
  private BrandRepository repository;

  public BrandService(BrandRepository repository) {
    this.repository = repository;
  }

  public List<Brand> getAllBrands() {
    List<Brand> brands = repository.findAll();
    return brands;
  }

  public Page<Brand> listBrandsByPage(int pageNum, String sortField, String sortOrder, String keyword) {

    Sort sort = Sort.by(sortField);
    sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

    PageRequest pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);

    if (keyword != null) {
      return repository.search(keyword, pageable);
    }
    return repository.findAll(pageable);

  }

  public Brand save(Brand brand) {
    return repository.save(brand);
  }

  public Brand getBrandById(Integer id) throws BrandNotFoundException {

    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new BrandNotFoundException("Could not found any brand with ID: " + id);
    }
  }

  public void delete(Integer id) throws BrandNotFoundException {
    Long countById = repository.countById(id);

    if (countById == null || countById == 0) {
      throw new BrandNotFoundException("Could not found any brand with ID: " + id);
    }
    repository.deleteById(id);
  }

  public Brand editBrandById(Integer id) throws BrandNotFoundException {
    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new BrandNotFoundException("Could not find any brand with ID: " + id);
    }
  }

  public String checkUnique(Integer id, String name) {
    boolean isCreatingNew = (id == null || id == 0);

    Brand brandByName = repository.findByName(name);

    if (isCreatingNew) {
      if (brandByName != null) {
        return "DuplicateName";
      }
    }
    return "OK";
  }

}
