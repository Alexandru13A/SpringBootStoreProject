package ro.store.admin.user.test.CountryAndState;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ro.store.admin.countries_states_backend.CountryRepository;
import ro.store.admin.countries_states_backend.StatesRepository;
import ro.store.common.entity.Country;
import ro.store.common.entity.state.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CountryRepository countryRepository;
  @Autowired
  private StatesRepository statesRepository;

  

  @Test
  @WithMockUser(username = "admin@gmail.com ", password = "Alex12345", roles = "ADMIN")
  public void testListByCountry() throws Exception {
    Integer countryId = 1;
    String url = "/states/list_by_country/" + countryId;

    MvcResult result = mockMvc.perform(get(url))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    State[] states = objectMapper.readValue(jsonResponse, State[].class);

    assertThat(states).hasSizeGreaterThan(0);

  }

  @Test
  @WithMockUser(username = "admin@gmail.com ", password = "Alex12345", roles = "ADMIN")
  public void testCreateState() throws Exception {
    String url = "/states/save";
    Integer countryId = 2;
    Country country = countryRepository.findById(countryId).get();
    State state = new State("North America", country);

    MvcResult result = mockMvc.perform(post(url).contentType("application/json")
        .content(objectMapper.writeValueAsString(state))
        .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String response = result.getResponse().getContentAsString();
    Integer stateId = Integer.parseInt(response);
    Optional<State> findById = statesRepository.findById(stateId);

    assertThat(findById.isPresent());
  }

  @Test
  @WithMockUser(username = "admin@gmail.com ", password = "Alex12345", roles = "ADMIN")
  public void testUpdateState() throws JsonProcessingException, Exception {
    String url = "/states/save";
    Integer stateId = 1;
    String stateName = "Alaska";

    State state = statesRepository.findById(stateId).get();
    state.setName(stateName);

    mockMvc.perform(post(url).contentType("application/json")
        .content(objectMapper.writeValueAsString(state))
        .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(String.valueOf(stateId)));

    Optional<State> findByOd = statesRepository.findById(stateId);
    assertThat(findByOd.isPresent());

    State updatedState = findByOd.get();
    assertThat(updatedState.getName()).isEqualTo(stateName);

  }

  @Test
  @WithMockUser(username = "admin@gmail.com ", password = "Alex12345", roles = "ADMIN")
  public void testDeleteState() throws Exception {
    Integer stateId = 1;
    String url = "/states/delete/" + stateId;

    mockMvc.perform(get(url)).andExpect(status().isOk());
    Optional<State> findById = statesRepository.findById(stateId);
    assertThat(findById).isNotPresent();

  }

}
