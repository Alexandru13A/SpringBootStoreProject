package ro.store.common.entity.order;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ro.store.common.entity.Address;
import ro.store.common.entity.Customer.Customer;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

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

  @Column(name = "address_line_2", length = 64)
  private String address2;

  @Column(nullable = false, length = 45)
  private String city;

  @Column(nullable = false, length = 45)
  private String state;

  @Column(name = "postal_code", nullable = false, length = 45)
  private String postalCode;

  @Column(nullable = false, length = 45)
  private String country;

  private Date orderTime;

  private float shippingCost;
  private float productCost;
  private float subtotal;
  private float tax;
  private float total;

  private int deliverDays;
  private Date deliverDate;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private Set<OrderDetail> orderDetails = new HashSet<>();

  @Transient
  public String getDestination() {
    String destination = city + ", ";

    if (state != null && !state.isEmpty())
      destination += state + ", ";
    destination += country;

    return destination;
  }

  public void copyAddressFromCustomer() {
    setFirstName(customer.getFirstName());
    setLastName(customer.getLastName());
    setPhoneNumber(customer.getPhoneNumber());
    setAddress1(customer.getAddressLine1());
    setAddress2(customer.getAddressLine2());
    setCity(customer.getCity());
    setCountry(customer.getCountry().getName());
    setPostalCode(customer.getPostalCode());
    setState(customer.getState());

  }

  public void copyShippingAddress(Address address) {
    setFirstName(address.getFirstName());
    setLastName(address.getLastName());
    setPhoneNumber(address.getPhoneNumber());
    setAddress1(address.getAddress1());
    setAddress2(address.getAddress2());
    setCity(address.getCity());
    setCountry(address.getCountry().getName());
    setPostalCode(address.getPostalCode());
    setState(address.getState());
  }

  @Transient
  public String getShippingAddress(){
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

    address += ", Country: " + country;

    if (!postalCode.isEmpty())
      address += ". Postal Code:  " + postalCode;
    if (!phoneNumber.isEmpty())
      address += ". Phone Number:  " + phoneNumber;

    return address;
  }

}
