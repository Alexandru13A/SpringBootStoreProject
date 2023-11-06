package ro.store.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.store.common.entity.Customer.Customer;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "first_name", nullable = false, length = 45)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 45)
  private String lastName;

  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @Column(name = "address_line_1", nullable = false, length = 64)
  private String address1;

  @Column(name = "address_line_2",  length = 64)
  private String address2;

  @Column(nullable = false, length = 45)
  private String city;

  @Column(nullable = false, length = 45)
  private String state;

  @Column(name = "postal_code", nullable = false, length = 45)
  private String postalCode;

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Column(name = "default_address")
  private boolean defaultForShipping;

  @Override
  public String toString(){
    String address = firstName;
    if (lastName != null && !lastName.isEmpty())
      address += " " + lastName;

    if (!address1.isEmpty())
      address += ",  " + address1;

    if (address2 != null && !address2.isEmpty())
      address += ",  " + address2;

    if (!city.isEmpty())
      address += ", City: " + city;

    if (state != null && !state.isEmpty())
      address += ", State: " + state;

    address += ", Country: " + country.getName();

    if (!postalCode.isEmpty())
      address += ". Postal Code:  " + postalCode;
    if (!phoneNumber.isEmpty())
      address += ". Phone Number:  " + phoneNumber;

    return address;
  }

}
