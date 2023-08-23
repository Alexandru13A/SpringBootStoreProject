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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "categories")

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

  @OneToOne
  @JoinColumn(name = "parent_id")
  private Category parent;

  @OneToMany(mappedBy = "parent")
  private Set<Category> children = new HashSet<>();

  public Category() {
  }

  public Category(Integer id) {
    this.id = id;
  }

  public static Category copyIdAndName(Category category) {
    Category copyCategory = new Category();
    copyCategory.setId(category.getId());
    copyCategory.setName(category.getName());

    return copyCategory;
  }

  public static Category copyIdAndName(Integer id , String name) {
    Category copyCategory = new Category();
    copyCategory.setId(id);
    copyCategory.setName(name);
    return copyCategory;
  }



  public static Category copyFull(Category category){
    Category copyCategory = new Category();
    copyCategory.setId(category.getId());
    copyCategory.setName(category.getName());
    copyCategory.setImage(category.getImage());
    copyCategory.setAlias(category.getAlias());
    copyCategory.setEnabled(category.getEnabled());
    return copyCategory;
  }

  public static Category copyFull(Category category,String name){
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

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getImage() {
    return this.image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Category getParent() {
    return this.parent;
  }

  public void setParent(Category parent) {
    this.parent = parent;
  }

  public Set<Category> getChildren() {
    return this.children;
  }

  public void setChildren(Set<Category> children) {
    this.children = children;
  }

  @Transient
  public String getImagePath() {
    if(this.id == null) return "/images/status/photo.png";

    return "category-images/" + this.id + "/" + this.image;

  }





}
