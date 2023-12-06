package ro.store.shipping;

import org.springframework.data.repository.CrudRepository;

import ro.store.common.entity.Country;
import ro.store.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate,Integer> {

  public ShippingRate findByCountryAndState(Country country, String state);
  
}
