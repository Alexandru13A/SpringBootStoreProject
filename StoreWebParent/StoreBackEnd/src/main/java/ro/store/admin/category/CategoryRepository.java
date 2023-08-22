package ro.store.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ro.store.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  @Query("SELECT c FROM Category c WHERE c.name =: name")
  public Category getCategoryByName(@Param("name") String name);

  public Long countById(Integer id);

  @Query("UPDATE Category c SET c.enabled =?2 WHERE c.id=?1 ")
  @Modifying
  public void updateStatus(Integer id, boolean enabled);

  @Query("SELECT c FROM Category c WHERE CONCAT(c.id,' ',c.name,' ',c.alias) LIKE %?1%")
  public Page<Category> findAll(String keyword, Pageable pageable);


  @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
  public List<Category> findRootCategories();

}