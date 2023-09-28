package ro.store.admin.countries_states_backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.store.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

  public List<Country> findAllByOrderByNameAsc();

}
