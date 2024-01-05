package ro.store.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.entity.Product.Product;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Transient
  private float shippingCost;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private int quantity;

  @Transient
  public float getSubtotal() {
    return product.getDiscountPrice() * quantity;
  }

  @Transient
  public float getShippingCost() {
    return shippingCost;
  }

  public void setShippingCost(float shippingCost) {
    this.shippingCost = shippingCost;
  }

}
