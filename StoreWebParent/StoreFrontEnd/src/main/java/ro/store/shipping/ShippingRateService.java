package ro.store.shipping;

import org.springframework.stereotype.Service;

import ro.store.common.entity.Address;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.Customer.Customer;

@Service
public class ShippingRateService {

  private ShippingRateRepository shippingRateRepository;

  public ShippingRateService(ShippingRateRepository shippingRateRepository) {
    this.shippingRateRepository = shippingRateRepository;
  }

  public ShippingRate getShippingRateForCustomer(Customer customer) {

    String state = customer.getState();
    if (state == null || state.isEmpty()) {
      state = customer.getCity();
    }
    return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
  }

  public ShippingRate getShippingRateForAddress(Address address) {

    String state = address.getState();
    if (state == null || state.isEmpty()) {
      state = address.getCity();
    }
    return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
  }

}
