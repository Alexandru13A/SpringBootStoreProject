package ro.store.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Address;
import ro.store.common.entity.Customer.Customer;

public interface AddressRepository extends JpaRepository<Address, Integer> {

  public List<Address> findByCustomer(Customer customer);

  @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
  public Address findByIdAndCustomer(Integer addressId, Integer customerId);

  @Query("DELETE  FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
  @Modifying
  public void deleteByIdAndCustomer(Integer addressId, Integer customerId);

  @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
  @Modifying
  public void setDefaultAddress(Integer addressId);

  @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != ?1 AND a.customer.id =?2")
  @Modifying
  public void setNonDefaultForOthers(Integer defaultAddressId, Integer customerId);

  @Query("SELECT a FROM Address a WHERE a.customer =?1 AND a.defaultForShipping = true")
  public Address findDefaultByCustomer(Integer customerId);

}
