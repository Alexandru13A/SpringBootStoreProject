package ro.store.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.countries_states_backend.CountryRepository;
import ro.store.admin.product.ProductRepository;
import ro.store.common.entity.Country;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.Product.Product;
import ro.store.common.exception.rate.ShippingRateAlreadyExistsException;
import ro.store.common.exception.rate.ShippingRateNotFoundException;

@Service
@Transactional
public class ShippingRateService {

  public static final int RATES_PER_PAGE = 10;
  private static final int DIM_DIVISOR = 139;

  private ShippingRateRepository shippingRateRepository;
  private CountryRepository countryRepository;
  private ProductRepository productRepository;

  public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository,ProductRepository productRepository) {
    this.shippingRateRepository = shippingRateRepository;
    this.countryRepository = countryRepository;
    this.productRepository = productRepository;
  }

  public void listByPage(int pageNum, PagingAndSortingHelper helper) {
    helper.listEntities(pageNum, RATES_PER_PAGE, shippingRateRepository);
  }

  public List<Country> listAllCountries() {
    return countryRepository.findAllByOrderByNameAsc();
  }

  public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {

    ShippingRate rateInDb = shippingRateRepository.findByCountryAndState(rateInForm.getCountry().getId(),
        rateInForm.getState());

    boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDb != null;
    boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDb != null;

    if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
      throw new ShippingRateAlreadyExistsException("There's already a rate for the destination: "
          + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
    }
    shippingRateRepository.save(rateInForm);

  }

  public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
    try {
      return shippingRateRepository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
    }
  }

  public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
    Long count = shippingRateRepository.countById(id);
    if (count == null || count == 0) {
      throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
    }
    shippingRateRepository.updateCODSupport(id, codSupported);
  }

  public void delete(Integer id) throws ShippingRateNotFoundException {
    Long count = shippingRateRepository.countById(id);
    if (count == null || count == 0) {
      throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
    }

    shippingRateRepository.deleteById(id);
  }

  public float calculateShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {

    ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);

    if(shippingRate == null){
      throw new ShippingRateNotFoundException("No shipping rate found for the given destination. You have to enter shipping cost manually ");
    }

    Product product = productRepository.findById(productId).get();

    float dimWeight = (product.getLength() * product.getWidth()*product.getHeight()) / DIM_DIVISOR;
    float finalWeight= product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

    return finalWeight*shippingRate.getRate();
  }

}
