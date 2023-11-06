package ro.store.address;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.common.entity.Address;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {

  @Autowired
  private AddressRepository repository;

  @Test
  public void testAddNew() {

    Integer customerId = 14;
    Integer countryId = 2;

    Address address = new Address();
    address.setCustomer(new Customer(customerId));
    address.setCountry(new Country(countryId));
    address.setFirstName("Vasile");
    address.setLastName("Alexandru");
    address.setPhoneNumber("1234-567-891");
    address.setAddress1("212 Mongrove Lane");
    address.setCity("New York");
    address.setState("New York");
    address.setPostalCode("123445");

    Address savedAddress = repository.save(address);

    assertThat(savedAddress).isNotNull();
    assertThat(savedAddress.getId()).isGreaterThan(0);

  }
}
