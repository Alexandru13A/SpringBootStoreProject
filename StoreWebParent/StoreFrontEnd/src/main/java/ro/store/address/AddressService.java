package ro.store.address;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.common.entity.Address;
import ro.store.common.entity.Customer.Customer;
import ro.store.customer.CustomerService;

@Service
@Transactional
public class AddressService {

  private AddressRepository addressRepository;

  public AddressService(AddressRepository addressRepository, CustomerService customerService) {
    this.addressRepository = addressRepository;
  }

  public List<Address> getAllAddresses(Customer customer) {
    return addressRepository.findByCustomer(customer);
  }

  public void save(Address address) {
    addressRepository.save(address);
  }

  public Address get(Integer addressId, Integer customerId) {
    return addressRepository.findByIdAndCustomer(addressId, customerId);
  }

  public void delete(Integer addressId, Integer customerId) {
    addressRepository.deleteByIdAndCustomer(addressId, customerId);
  }

  public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {

    if(defaultAddressId > 0){
      addressRepository.setDefaultAddress(defaultAddressId);
    }
    addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
  }

}
