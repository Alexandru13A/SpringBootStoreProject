package ro.store.customer;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.AuthenticationType;
import ro.store.common.entity.Customer.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {

  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testCreateCustomer() {
    Integer countryId = 2;
    Country country = entityManager.find(Country.class, countryId);

    Customer customer = new Customer();
    customer.setCountry(country);
    customer.setFirstName("Alexandru");
    customer.setLastName("Vasile");
    customer.setPassword("Alex1234");
    customer.setEmail("alexandru@gmail.com");
    customer.setPhoneNumber("0766-555-444");
    customer.setAddressLine1("Valcea,str.Viilor nr.13,bl.D2, sc.A, ap.12");
    customer.setCity("Valcea");
    customer.setState("Arges");
    customer.setPostalCode("111111");
    customer.setCreatedTime(new Date());

    Customer savedCustomer = customerRepository.save(customer);

    assertThat(savedCustomer).isNotNull();
    assertThat(savedCustomer.getId()).isGreaterThan(0);

  }

  @Test
  public void testUpdateAuthenticationType() {
    Integer id = 14;
    customerRepository.updateAuthenticationType(id, AuthenticationType.FACEBOOK);
    Customer customer = customerRepository.findById(id).get();

    assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.FACEBOOK);

  }

}
