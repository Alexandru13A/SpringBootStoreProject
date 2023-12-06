package ro.store.shipping;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ro.store.common.entity.Country;
import ro.store.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShippingRateRepositoryTest {

  @Autowired
  private ShippingRateRepository repository;

  @Test
  public void testFindByCountryAndState() {

    Country usa = new Country(2);

    String state = "Alabama";

    ShippingRate shippingRate = repository.findByCountryAndState(usa, state);

    assertThat(shippingRate).isNotNull();
    System.out.println(shippingRate);

  }

}
