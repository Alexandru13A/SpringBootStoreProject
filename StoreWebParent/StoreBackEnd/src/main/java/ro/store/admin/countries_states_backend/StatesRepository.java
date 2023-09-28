package ro.store.admin.countries_states_backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.store.common.entity.Country;
import ro.store.common.entity.state.State;

public interface StatesRepository extends JpaRepository<State, Integer> {

  public List<State> findByCountryOrderByNameAsc(Country country);

}
