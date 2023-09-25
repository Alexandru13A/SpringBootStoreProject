package ro.store.admin.CountryAndStates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ro.store.common.entity.Country;
import ro.store.common.entity.State;

@RestController
public class StatesRestController {

  @Autowired
  private StatesRepository statesRepository;

  @GetMapping("/states/list_by_country/{id}")
  public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {

    List<State> listStates = statesRepository.findByCountryOrderByNameAsc(new Country(countryId));
    List<StateDTO> result = new ArrayList<>();

    for (State state : listStates) {
      result.add(new StateDTO(state.getId(), state.getName()));
    }
    return result;
  }

  @PostMapping("/states/save")
  public String saveState(@RequestBody State state) {
    State savedState = statesRepository.save(state);
    return String.valueOf(savedState.getId());
  }

  @DeleteMapping("/states/delete/{id}")
  public void deleteState(@PathVariable("id") Integer id) {
    statesRepository.deleteById(id);
  }

}
