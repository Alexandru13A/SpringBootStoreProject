package ro.store.common.entity;

import java.util.Date;

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

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public String toString() {
    return "Name: " + firstName + " " + lastName + "|" + "Email: " + email + "|" + "City: " + city + "|" + "State: "
        + state + "|" + "Country: " + country;
  }

}