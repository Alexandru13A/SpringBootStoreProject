package ro.store.common.entity.Customer;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ro.store.common.entity.Country;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 60)
  private String email;

  @Column(nullable = false, length = 70)
  private String password;

  @Column(nullable = false, name = "first_name", length = 45)
  private String firstName;

  @Column(nullable = false, name = "last_name", length = 45)
  private String lastName;

  @Column(nullable = false, length = 15)
  private String phoneNumber;

  @Column(nullable = false, name = "address_1", length = 70)
  private String addressLine1;

  @Column(name = "address_2", length = 70)
  private String addressLine2;

  @Column(nullable = false, length = 45)
  private String city;

  @Column(nullable = false, length = 45)
  private String state;

  @Column(nullable = false, name = "postal_code", length = 45)
  private String postalCode;

  @Column(name = "verification_code", length = 64)
  private String verificationCode;

  private boolean enabled;

  @Column(name = "created_time", length = 45)
  private Date createdTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "authenticationType", length = 10)
  private AuthenticationType authenticationType;

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  @Column(name = "reset_password_token", length = 30)
  private String resetPasswordToken;

  public Customer(Integer id) {
    this.id = id;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Transient
  public String getAddress() {
    String address = firstName;
    if (lastName != null && !lastName.isEmpty())
      address += " " + lastName;

    if (!addressLine1.isEmpty())
      address += ",  " + addressLine1;

    if (addressLine2 != null && !addressLine2.isEmpty())
      address += ",  " + addressLine2;

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

  @Override
  public String toString() {
    return "Name: " + firstName + " " + lastName + "|" + "Email: " + email + "|" + "City: " + city + "|" + "State: "
        + state + "|" + "Country: " + country;
  }

}
