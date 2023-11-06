package ro.store.address.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import ro.store.address.AddressService;
import ro.store.common.entity.Address;
import ro.store.common.entity.Country;
import ro.store.common.entity.Customer.Customer;
import ro.store.countries_states_frontend.CountryRepository;
import ro.store.customer.CustomerService;
import ro.store.utility.Utility;

@Controller
public class AddressController {

  private AddressService addressService;
  private CustomerService customerService;
  private CountryRepository countryRepository;

  public AddressController(AddressService addressService, CustomerService customerService,
      CountryRepository countryRepository) {
    this.addressService = addressService;
    this.customerService = customerService;
    this.countryRepository = countryRepository;
  }

  @GetMapping("/address_book")
  public String listAddressBook(Model model, HttpServletRequest request) {
    Customer customer = getAuthenticatedCustomer(request);
    List<Address> listAddresses = addressService.getAllAddresses(customer);

    boolean usePrimaryAddressAsDefault = true;

    for (Address address : listAddresses) {
      if (address.isDefaultForShipping()) {
        usePrimaryAddressAsDefault = false;
        break;
      }
    }

    model.addAttribute("listAddresses", listAddresses);
    model.addAttribute("customer", customer);
    model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

    return "address/addresses";
  }

  private Customer getAuthenticatedCustomer(HttpServletRequest request) {
    String email = Utility.getEmailOfAuthenticatedCustomer(request);
    return customerService.getCustomerByEmail(email);
  }

  @GetMapping("/address_book/new")
  public String createNewAddress(Model model) {
    List<Country> countries = countryRepository.findAllByOrderByNameAsc();

    model.addAttribute("address", new Address());
    model.addAttribute("countries", countries);
    model.addAttribute("pageTitle", "Add new Address");

    return "address/address_form";
  }

  @PostMapping("/address_book/save")
  public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes redirectAttributes) {

    Customer customer = getAuthenticatedCustomer(request);
    address.setCustomer(customer);
    addressService.save(address);

    redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");
    return "redirect:/address_book";
  }

  @GetMapping("/address_book/edit/{id}")
  public String editAddress(@PathVariable("id") Integer id, Model model, HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    Customer customer = getAuthenticatedCustomer(request);
    List<Country> countries = countryRepository.findAllByOrderByNameAsc();

    Address address = addressService.get(id, customer.getId());
    model.addAttribute("address", address);
    model.addAttribute("countries", countries);
    model.addAttribute("pageTitle", "Update address with ID: " + id);

    return "address/address_form";
  }

  @GetMapping("/address_book/delete/{id}")
  public String deleteAddress(@PathVariable("id") Integer addressId, HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    Customer customer = getAuthenticatedCustomer(request);
    addressService.delete(addressId, customer.getId());
    redirectAttributes.addFlashAttribute("message", "Address with ID: " + addressId + " has been deleted");

    return "redirect:/address_book";
  }

  @GetMapping("/address_book/default/{id}")
  public String setDefaultAddress(@PathVariable("id") Integer addressId, HttpServletRequest request) {

    Customer customer = getAuthenticatedCustomer(request);
    addressService.setDefaultAddress(addressId, customer.getId());

    return "redirect:/address_book";

  }

}
