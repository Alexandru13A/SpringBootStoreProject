package ro.store.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.common.entity.Category;

@Service
@Transactional
public class CategoryService {

  public static final int CATEGORIES_PER_PAGE = 5;
  private final CategoryRepository repository;

  public CategoryService(CategoryRepository repository) {
    this.repository = repository;
  }

  // GET CATEGORY BY NAME
  public Category findCategoryByName(String name) {
    return repository.getCategoryByName(name);
  }

  // GET ALL CATEGORIES HIERARCHIAL
  public List<Category> getAllCategories() {
    List<Category> rootCategories = repository.findRootCategories();
    return listHierarchialCategories(rootCategories);
  }

  private List<Category> listHierarchialCategories(List<Category> rootCategories) {
    List<Category> hierarchialCategories = new ArrayList<>();

    for (Category rootCategory : rootCategories) {
      hierarchialCategories.add(Category.copyFull(rootCategory));

      Set<Category> children = rootCategory.getChildren();
      for (Category subCategory : children) {
        String name = "--" + subCategory.getName();
        hierarchialCategories.add(Category.copyFull(subCategory, name));
        listSubHierarchialCategories(hierarchialCategories, subCategory,1 );
      }
    }

    return hierarchialCategories;

  }

  private void listSubHierarchialCategories(List<Category> hierarchialCategories, Category parent, int subLevel) {
    Set<Category> children = parent.getChildren();
    int newSubLevel = subLevel + 1;
    for (Category subCategory : children) {
      String name = " ";
      for (int i = 0; i < newSubLevel; i++) {
        name += "--";
      }
      name += subCategory.getName();
      hierarchialCategories.add(Category.copyFull(subCategory, name));

      listSubHierarchialCategories(hierarchialCategories, subCategory, newSubLevel);
    }
  }

  // SAVE CATEGORY
  public Category saveCategory(Category category) {
    return repository.save(category);
  }

  // UPDATE CATEGORY STATUS
  public void updateStatus(Integer id, boolean status) {
    repository.updateStatus(id, status);
  }

  // GET CATEGORY BY ID

  public Category getCategoryById(Integer id) throws CategoryNotFoundException {
    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new CategoryNotFoundException("Category with this ID :" + id + " doesn't exist");

    }
  }

  // DELETE CATEGORY
  public void deleteCategory(Integer id) throws CategoryNotFoundException {
    Long countById = repository.countById(id);

    if (countById == null || countById == 0) {
      throw new CategoryNotFoundException("Could not found any category with ID : " + id);
    }
    repository.deleteById(id);

  }

  // LIST CATEGORIES BY PAGE
  public Page<Category> categoryListingByPage(int pageNumber, String sortField, String sortOrder, String keyword) {

    Sort sort = Sort.by(sortField);

    sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

    PageRequest pageable = PageRequest.of(pageNumber - 1, CATEGORIES_PER_PAGE, sort);

    if (keyword != null) {
      return repository.findAll(keyword, pageable);
    }

    return repository.findAll(pageable);

  }

  // CATEGORY LIST
  public List<Category> listCategoriesUsedInForm() {
    List<Category> categoriesUsedInForm = new ArrayList<>();
    Iterable<Category> categoriesFromDB = repository.findAll();

    for (Category category : categoriesFromDB) {
      if (category.getParent() == null) {
        categoriesUsedInForm.add(Category.copyIdAndName(category));

        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
          String name = "--" + subCategory.getName();
          categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(),name));
          listSubcategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
        }

      }
    }

    return categoriesUsedInForm;
  }

  private void listSubcategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
    int newSubLevel = subLevel + 1;
    Set<Category> children = parent.getChildren();

    for (Category subCategory : children) {
      String name = " ";
      for (int i = 0; i < newSubLevel; i++) {
        name += "--";
      }
      name += subCategory.getName();
      categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

      listSubcategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
    }
  }

  //GET CATEGORY BY ID FOR EDIT
  public Category editCategoryById(Integer id ) throws CategoryNotFoundException{
    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
    throw new CategoryNotFoundException("Could not find any category with id: "+id);
    }


  }

}