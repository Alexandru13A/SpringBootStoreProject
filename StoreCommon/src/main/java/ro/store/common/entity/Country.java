package ro.store.common.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ro.store.common.entity.state.State;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Country {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 80, nullable = false)
  private String name;

  @Column(length = 5, nullable = false)
  private String code;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "country")
  private Set<State> states;

  public Country(Integer id) {
    this.id = id;
  }

  public Country(Integer id, String name, String code) {
    this.id = id;
    this.name = name;
    this.code = code;
  }

  public Country(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public Country(String name) {
    this.name = name;
  }

}
