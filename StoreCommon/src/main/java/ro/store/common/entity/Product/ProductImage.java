package ro.store.common.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.store.common.constants.Constants;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  public ProductImage(String name, Product product) {
    this.name = name;
    this.product = product;
  }


  @Transient
  public String getImagePath() {
    return Constants.S3_BASE_URI+ "/product-images/" + product.getId() + "/extras/" + this.name;
  }

  //BEFORE AWS
  // @Transient
  // public String getImagePath() {
  //   return "/product-images/" + product.getId() + "/extras/" + this.name;
  // }

}
