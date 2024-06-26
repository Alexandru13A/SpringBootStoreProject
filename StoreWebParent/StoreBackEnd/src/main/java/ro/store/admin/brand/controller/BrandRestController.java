package ro.store.admin.brand.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.brand.BrandNotFoundException;
import ro.store.admin.brand.BrandService;
import ro.store.admin.brand.CategoryDTO;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;

@RestController
public class BrandRestController {

  @Autowired
  private BrandService service;

  @PostMapping("/brands/check_unique")
  public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
    return service.checkUnique(id, name);
  }

  @GetMapping("/brands/{id}/categories")
  public List<CategoryDTO> listCategoryByBrand(@PathVariable(name = "id") Integer id) throws BrandNotFoundException {
    List<CategoryDTO> listCategory = new ArrayList<>();
    try {
      Brand brand = service.getBrandById(id);
      Set<Category> categories = brand.getCategories();
      for (Category category : categories) {
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
        listCategory.add(categoryDTO);
      }
      return listCategory;

    } catch (BrandNotFoundException e) {
        throw new BrandNotFoundException("No brand found");
    }
  }

}