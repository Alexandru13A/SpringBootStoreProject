package ro.store.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

  @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
  public List<Category> findAllEnabled();

  @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
  public Category findByAliasEnabled(String alias);
}
