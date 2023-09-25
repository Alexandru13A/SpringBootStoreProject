package ro.store.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Currency {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 64, nullable = false)
  private String name;
  @Column(length = 3, nullable = false)
  private String symbol;
  @Column(length = 4, nullable = false)
  private String code;

  public Currency(String name, String symbol, String code) {
    this.name = name;
    this.symbol = symbol;
    this.code = code;
  }

  @Override
  public String toString() {
    return name + " - " + code + " - " + symbol;
  }

}
