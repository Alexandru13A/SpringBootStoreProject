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

import ro.store.admin.CountryAndStates.CountryRepository;
import ro.store.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerText {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  CountryRepository countryRepository;

  @Test
  @WithMockUser(username = "admin@gmail.com", password = "Alex12345", roles = "ADMIN")
  public void listCountries() throws Exception {
    String url = "/countries/list";
    MvcResult mvcResult = mockMvc.perform(get(url))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    String jsonResponse = mvcResult.getResponse().getContentAsString();
    System.out.println(jsonResponse);

    Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

    for (Country country : countries) {
      System.out.println(country);
    }

    assertThat(countries).hasSizeGreaterThan(0);

  }

  @Test
  @WithMockUser(username = "admin@gmail.com", password = "Alex12345", roles = "ADMIN")
  public void testCreateCountry() throws JsonProcessingException, Exception {
    String url = "/countries/save";
    String countryName = "Germany";
    String countryCode = "DE";

    Country country = new Country(countryName, countryCode);

    MvcResult result = mockMvc
        .perform(post(url).contentType("application/json")
            .content(objectMapper.writeValueAsString(country))
            .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    String response = result.getResponse().getContentAsString();
    Integer countryId = Integer.parseInt(response);
    Optional<Country> findById = countryRepository.findById(countryId);
    assertThat(findById.isPresent());

    Country savedCountry = findById.get();
    assertThat(savedCountry.getName()).isEqualTo(countryName);
  }

  @Test
  @WithMockUser(username = "admin@gmail.com", password = "Alex12345", roles = "ADMIN")
  public void testUpdateCountry() throws JsonProcessingException, Exception {
    String url = "/countries/save";
    Integer countryId = 5;
    String countryName = "China";
    String countryCode = "CN";

    Country country = new Country(countryId, countryName, countryCode);

    mockMvc
        .perform(post(url).contentType("application/json")
            .content(objectMapper.writeValueAsString(country))
            .with(csrf()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(String.valueOf(countryId)));

    Optional<Country> findById = countryRepository.findById(countryId);
    assertThat(findById.isPresent());

    Country savedCountry = findById.get();
    assertThat(savedCountry.getName()).isEqualTo(countryName);
  }

  @Test
  @WithMockUser(username = "admin@gmail.com", password = "Alex12345", roles = "ADMIN")
  public void testDeleteCountry() throws Exception {
    Integer countryId = 5;
    String url = "/countries/delete/" + countryId;
    mockMvc.perform(get(url)).andExpect(status().isOk());

    Optional<Country> findById = countryRepository.findById(countryId);

    assertThat(findById).isNotPresent();

  }

}
