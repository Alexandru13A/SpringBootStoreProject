package ro.store.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.store.common.constants.Constants;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 128, nullable = false, unique = true)
  private String name;

  @Column(length = 64, nullable = false, unique = true)
  private String alias;

  @Column(length = 128)
  private String image;

  private boolean enabled;

  @Column(name = "all_parents_ids", length = 256, nullable = true)
  private String allParentsIds;

  @OneToOne
  @JoinColumn(name = "parent_id")
  private Category parent;

  @OneToMany(mappedBy = "parent")
  @OrderBy("name asc")
  private Set<Category> children = new HashSet<>();

  public Category(Integer id) {
    this.id = id;
  }

  public static Category copyIdAndName(Category category) {
    Category copyCategory = new Category();
    copyCategory.setId(category.getId());
    copyCategory.setName(category.getName());

    return copyCategory;
  }

  public static Category copyIdAndName(Integer id, String name) {
    Category copyCategory = new Category();
    copyCategory.setId(id);
    copyCategory.setName(name);
    return copyCategory;
  }

  public static Category copyFull(Category category) {
    Category copyCategory = new Category();
    copyCategory.setId(category.getId());
    copyCategory.setName(category.getName());
    copyCategory.setImage(category.getImage());
    copyCategory.setAlias(category.getAlias());
    copyCategory.setEnabled(category.isEnabled());
    copyCategory.setHasChildren(category.getChildren().size() > 0);
    return copyCategory;
  }

  public static Category copyFull(Category category, String name) {
    Category copyCategory = Category.copyFull(category);
    copyCategory.setName(name);
    return copyCategory;

  }

  public Category(String name) {
    this.name = name;
    this.alias = name;
    this.image = "default.png";
  }

  public Category(String name, Category parent) {
    this(name);
    this.parent = parent;
  }

  public Category(Integer id, String name, String alias) {
    super();
    this.id = id;
    this.name = name;
    this.alias = alias;
  }

  @Transient
  private boolean hasChildren;

  @Transient
  public String getImagePath() {
    if (image == null)
      return "/images/status/photo.png";

    return Constants.S3_BASE_URI+ "/category-images/" + this.id + "/" + this.image;

  }

  //BEFORE AWS
  // @Transient
  // public String getImagePath() {
  //   if (image == null)
  //     return "/images/status/photo.png";

  //   return "/category-images/" + this.id + "/" + this.image;

  // }

  @Override
  public String toString() {
    return this.name;
  }

}
