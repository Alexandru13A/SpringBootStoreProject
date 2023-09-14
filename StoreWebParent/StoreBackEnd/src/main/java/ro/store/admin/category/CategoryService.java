package ro.store.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.admin.category.util.CategoryPageInfo;
import ro.store.common.entity.Category;

@Service
@Transactional
public class CategoryService {

  public static final int CATEGORIES_PER_PAGE = 4;
  @Autowired
  private CategoryRepository repository;

  public CategoryService(CategoryRepository repository) {
    this.repository = repository;
  }

  // GET ALL CATEGORIES HIERARCHIAL
  public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortOrder, String keyword) {
    Sort sort = Sort.by("name");

    if (sortOrder.equals("asc")) {
      sort = sort.ascending();
    } else if (sortOrder.equals("desc")) {
      sort = sort.descending();
    }

    PageRequest pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);

    Page<Category> pageCategories = null;

    if (keyword != null && !keyword.isEmpty()) {
      pageCategories = repository.search(keyword, pageable);
    } else {
      pageCategories = repository.findRootCategories(pageable);
    }

    List<Category> rootCategories = pageCategories.getContent();

    pageInfo.setTotalElements(pageCategories.getTotalElements());
    pageInfo.setTotalPages(pageCategories.getTotalPages());

    if (keyword != null && !keyword.isEmpty()) {
      List<Category> searchResult = pageCategories.getContent();
      for (Category category : searchResult) {
        category.setHasChildren(category.getChildren().size() > 0);
      }
      return searchResult;

    } else {
      return listHierarchialCategories(rootCategories, sortOrder);
    }
  }

  // LIST IN HIERARCHIAL MODE IN THE CATEGORIES TABLE
  private List<Category> listHierarchialCategories(List<Category> rootCategories, String sortOrder) {
    List<Category> hierarchialCategories = new ArrayList<>();

    for (Category rootCategory : rootCategories) {
      hierarchialCategories.add(Category.copyFull(rootCategory));

      Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortOrder);
      for (Category subCategory : children) {
        String name = "--" + subCategory.getName();
        hierarchialCategories.add(Category.copyFull(subCategory, name));
        listSubHierarchialCategories(hierarchialCategories, subCategory, 1, sortOrder);
      }
    }

    return hierarchialCategories;

  }

  private void listSubHierarchialCategories(List<Category> hierarchialCategories, Category parent, int subLevel,
      String sortOrder) {
    Set<Category> children = sortSubCategories(parent.getChildren(), sortOrder);
    int newSubLevel = subLevel + 1;
    for (Category subCategory : children) {
      String name = " ";
      for (int i = 0; i < newSubLevel; i++) {
        name += "--";
      }
      name += subCategory.getName();
      hierarchialCategories.add(Category.copyFull(subCategory, name));

      listSubHierarchialCategories(hierarchialCategories, subCategory, newSubLevel, sortOrder);
    }
  }

  // SAVE CATEGORY
  public Category saveCategory(Category category) {
    Category parent = category.getParent();
    if(parent != null){
      String allParentsIds = parent.getAllParentsIds() == null ? "-" :parent.getAllParentsIds();
      allParentsIds += String.valueOf(parent.getId()+"-");
      category.setAllParentsIds(allParentsIds);
    }
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

  // CATEGORIES LIST FROM CREATE AND UPDATE FORM (TO SET PARENT CATEGORY)
  public List<Category> listCategoriesUsedInForm() {
    List<Category> categoriesUsedInForm = new ArrayList<>();
    Iterable<Category> categories = repository.findAll();

    for (Category category : categories) {
      if (category.getParent() == null) {
        categoriesUsedInForm.add(Category.copyIdAndName(category));
        Set<Category> children = category.getChildren();

        for (Category subCategory : children) {
          String name = "--" + subCategory.getName();
          categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
          listChildren(categoriesUsedInForm, subCategory, 1);
        }
      }
    }
    return categoriesUsedInForm;
  }

  private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
    int newSubLevel = subLevel + 1;
    Set<Category> children = parent.getChildren();

    for (Category subCategory : children) {
      String name = "";
      for (int i = 0; i < newSubLevel; i++) {
        name += "--";
      }
      name += subCategory.getName();
      categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

      listChildren(categoriesUsedInForm, subCategory, newSubLevel);
    }
  }

  // GET CATEGORY BY ID FOR EDIT
  public Category editCategoryById(Integer id) throws CategoryNotFoundException {
    try {
      return repository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new CategoryNotFoundException("Could not find any category with id: " + id);
    }

  }

  // CHECK FOR CATEGORY NAME AND ALIAS TO BE UNIQUE
  public String checkUnique(Integer id, String name, String alias) {
    boolean isCreatingNew = (id == null || id == 0);

    Category categoryByName = repository.findByName(name);

    if (isCreatingNew) {
      if (categoryByName != null) {
        return "DuplicateName";
      } else {
        Category categoryByAlias = repository.findByAlias(alias);
        if (categoryByAlias != null) {
          return "DuplicateAlias";
        }
      }
    } else {
      if (categoryByName != null && categoryByName.getId() != id) {
        return "DuplicateName";
      }
      Category categoryByAlias = repository.findByAlias(alias);
      if (categoryByAlias != null && categoryByAlias.getId() != id) {
        return "DuplicateAlias";
      }

    }

    return "OK";
  }

  private SortedSet<Category> sortSubCategories(Set<Category> children, String sortOrder) {
    SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
      @Override
      public int compare(Category cat1, Category cat2) {
        if (sortOrder.equals("asc")) {
          return cat1.getName().compareTo(cat2.getName());
        } else {
          return cat2.getName().compareTo(cat1.getName());
        }
      }
    });
    sortedChildren.addAll(children);
    return sortedChildren;

  }

}