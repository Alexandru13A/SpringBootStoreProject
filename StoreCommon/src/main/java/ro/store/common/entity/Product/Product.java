package ro.store.common.entity.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, length = 255, nullable = false)
  private String name;

  @Column(unique = true, length = 255, nullable = false)
  private String alias;

  @Column(length = 250, name = "product_image", nullable = false)
  private String mainImage;

  @Column(length = 512, nullable = false, name = "short_description")
  private String shortDescription;

  @Column(length = 4096, nullable = false, name = "full_description")
  private String fullDescription;

  @Column(name = "created_time")
  private Date timeWhenIsCreated;
  @Column(name = "updated_time")
  private Date timeWhenIsUpdated;

  private boolean enabled;
  @Column(name = "in_stock")
  private boolean inStock;

  private float cost;

  private float price;
  @Column(name = "discount_percent")
  private float discount;

  private float length;
  private float width;
  private float height;
  private float weight;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "brand_id")
  private Brand brand;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ProductImage> images = new HashSet<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductDetail> details = new ArrayList<>();


  public Product(Integer id){
    this.id = id;
  }


  @Transient
  public String getMainImagePath() {

    if (id == null || mainImage == null)
      return "/images/status/photo.png";

    return "/product-images/" + this.id + "/" + this.mainImage;
  }

  public void addExtraImage(String imageName) {
    this.images.add(new ProductImage(imageName, this));
  }

  public void addDetails(String name, String value) {
    this.details.add(new ProductDetail(name, value, this));
  }

  public void addDetails(Integer id, String name, String value) {
    this.details.add(new ProductDetail(id, name, value, this));
  }

  public boolean containsImageName(String imageName) {
    Iterator<ProductImage> iterator = images.iterator();
    while (iterator.hasNext()) {
      ProductImage image = iterator.next();
      if (image.getName().equals(imageName)) {
        return true;
      }
    }
    return false;
  }

  @Transient
  public String getShortName() {
    if (name.length() > 70) {
      return name.substring(0, 70).concat("...");
    }
    return name;
  }

  @Transient
  public float getDiscountPrice() {
    if (discount > 0) {
      return price * ((100 - discount) / 100);
    }
    return this.price;
  }

}
