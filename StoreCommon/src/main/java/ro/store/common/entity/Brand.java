package ro.store.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
public class Brand {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 45, unique = true)
  private String name;

  @Column(nullable = false, length = 128)
  private String logo;

  @ManyToMany
  @JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  public Brand(Integer id, String name, String logo, Set<Category> categories) {
    this.id = id;
    this.name = name;
    this.logo = logo;
    this.categories = categories;
  }

  public Brand(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
 

  public Set<Category> getCategories() {
    return this.categories;
  }

  public void setCategories(Set<Category> categories) {
    this.categories = categories;
  }

  @Transient
  public String getImagePath() {
    if (logo == null)
      return "/images/status/photo.png";

    return "/brand-logos/" + this.id + "/" + this.logo;
  }

  @Override
  public String toString() {
    return "{" +
        " id='" + getId() + "'" +
        ", name='" + getName() + "'" +
        ", logo='" + getLogo() + "'" +
        ", categories='" + getCategories() + "'" +
        "}";
  }

}
