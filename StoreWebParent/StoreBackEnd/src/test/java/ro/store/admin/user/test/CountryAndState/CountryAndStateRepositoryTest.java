package ro.store.admin.user.test.CountryAndState;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.countries_states_backend.CountryRepository;
import ro.store.admin.countries_states_backend.StatesRepository;
import ro.store.common.entity.Country;
import ro.store.common.entity.state.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryAndStateRepositoryTest {

  @Autowired
  private CountryRepository countryRepository;
  @Autowired
  private StatesRepository statesRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testCreateCountry() {
    Country country = countryRepository.save(new Country("India", "IN"));
    assertThat(country).isNotNull();
    assertThat(country.getId()).isGreaterThan(0);
  }

  @Test
  public void addMoreCountries() {

    List<Country> countries = Arrays.asList(new Country("United States", "US"),
        new Country("United Kingdom", "UK"),
        new Country("Vietnam", "VN"));

        countryRepository.saveAll(countries);

  }

  @Test
  public void testCreateStatesInIndia() {
    Integer countryId = 1;
    Country country = entityManager.find(Country.class, countryId);
    State state = statesRepository.save(new State("West Bengal", country));
    assertThat(state).isNotNull();
    assertThat(state.getId()).isGreaterThan(0);
  }

}
