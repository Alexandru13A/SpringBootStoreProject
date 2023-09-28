package ro.store.countries_states_frontend;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ro.store.common.entity.Country;
import ro.store.common.entity.state.State;
import ro.store.common.entity.state.StateDTO;

@RestController
public class StatesRestController {

  @Autowired
  private StatesRepository statesRepository;

  @GetMapping("/settings/list_states_by_country/{id}")
  public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {

    List<State> listStates = statesRepository.findByCountryOrderByNameAsc(new Country(countryId));
    List<StateDTO> result = new ArrayList<>();

    for (State state : listStates) {
      result.add(new StateDTO(state.getId(), state.getName()));
    }
    return result;
  }


}
