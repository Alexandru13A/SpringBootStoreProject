package ro.store.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Customer.AuthenticationType;
import ro.store.common.entity.Customer.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {

  @Query("SELECT c FROM Customer c WHERE c.email =?1")
  public Customer findByEmail(String email);

  @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
  public Customer findByVerificationCode(String verificationCode);

  @Query("UPDATE Customer c SET c.enabled = true , c.verificationCode = null WHERE c.id = ?1")
  @Modifying
  public void enabled(Integer id);

  @Query("UPDATE Customer c SET c.authenticationType=?2 WHERE c.id = ?1")
  @Modifying
  public void updateAuthenticationType(Integer customerId, AuthenticationType type);

  public Customer findByResetPasswordToken(String resetPasswordToken);

}
