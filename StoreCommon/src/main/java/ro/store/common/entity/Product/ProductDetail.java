package ro.store.common.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
@NoArgsConstructor
public class ProductDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, length = 255)
  private String value;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;


  public ProductDetail(Integer id, String name, String value, Product product) {
    this.id = id;
    this.name = name;
    this.value = value;
    this.product = product;
  }


  public ProductDetail(String name, String value, Product product) {
    this.name = name;
    this.value = value;
    this.product = product;
  }
}
