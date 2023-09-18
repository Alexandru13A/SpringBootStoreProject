package ro.store.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.store.common.entity.Category;
import ro.store.common.exception.category.CategoryNotFoundException;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository repository;

  public List<Category> noChildCategories() {
    List<Category> listNoChildCategories = new ArrayList<>();
    List<Category> listEnabledCategories = repository.findAllEnabled();

    listEnabledCategories.forEach(category -> {
      Set<Category> children = category.getChildren();
      if (children == null || children.size() == 0) {
        listNoChildCategories.add(category);
      }
    });

    return listNoChildCategories;
  }

  public Category getCategory(String alias) throws CategoryNotFoundException {
    Category category = repository.findByAliasEnabled(alias);
    if (category == null) {
      throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
    }
    return category;
  }

  public List<Category> getCategoryParents(Category child) {
    List<Category> parentList = new ArrayList<>();
    Category parent = child.getParent();

    while (parent != null) {
      parentList.add(0, parent);
      parent = parent.getParent();
    }
    parentList.add(child);

    return parentList;
  }

}
