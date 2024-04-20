package ro.store.common.entity.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import jakarta.persistence.OrderBy;
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
public class Order  {

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

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("updatedTime ASC")
	private List<OrderTrack> orderTracks = new ArrayList<>();

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

  public List<OrderTrack> getOrderTracks() {
		return orderTracks;
	}

	public void setOrderTracks(List<OrderTrack> orderTracks) {
		this.orderTracks = orderTracks;
	}
	
	@Transient
	public String getDeliverDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatter.format(this.deliverDate);
	}	
	
	public void setDeliverDateOnForm(String dateString) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
 		
		try {
			this.deliverDate = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 		
	}
	
	@Transient
	public String getRecipientName() {
		String name = firstName;
		if (lastName != null && !lastName.isEmpty()) name += " " + lastName;
		return name;
	}
	

	
	@Transient
	public boolean isCOD() {
		return paymentMethod.equals(PaymentMethod.COD);
	}
	
	@Transient
	public boolean isProcessing() {
		return hasStatus(OrderStatus.PROCESSING);
	}
	
	@Transient
	public boolean isPicked() {
		return hasStatus(OrderStatus.PICKED);
	}
	
	@Transient
	public boolean isShipping() {
		return hasStatus(OrderStatus.SHIPPING);
	}
	
	@Transient
	public boolean isDelivered() {
		return hasStatus(OrderStatus.DELIVERED);
	}

	@Transient
	public boolean isReturnRequested() {
		return hasStatus(OrderStatus.RETURN_REQUESTED);
	}	
	
	@Transient
	public boolean isReturned() {
		return hasStatus(OrderStatus.RETURNED);
	}	
	
	public boolean hasStatus(OrderStatus status) {
		for (OrderTrack aTrack : orderTracks) {
			if (aTrack.getStatus().equals(status)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Transient
	public String getProductNames() {
		String productNames = "";
		
		productNames = "<ul>";
		
		for (OrderDetail detail : orderDetails) {
			productNames += "<li>" + detail.getProduct().getShortName() + "</li>";			
		}
		
		productNames += "</ul>";
		
		return productNames;
	}	

}
