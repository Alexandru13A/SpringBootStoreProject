package ro.store.countries_states_frontend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ro.store.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

  public List<Country> findAllByOrderByNameAsc();

  @Query("SELECT c FROM Country c WHERE c.code = ?1")
  public Country findByCode(String code);

}
