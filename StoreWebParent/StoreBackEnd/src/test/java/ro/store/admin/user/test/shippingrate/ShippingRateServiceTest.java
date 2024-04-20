package ro.store.admin.user.test.shippingrate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.store.admin.product.ProductRepository;
import ro.store.admin.shippingrate.ShippingRateRepository;
import ro.store.admin.shippingrate.ShippingRateService;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.Product.Product;
import ro.store.common.exception.rate.ShippingRateNotFoundException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

public class ShippingRateServiceTest {

    @MockBean
    private ShippingRateRepository shippingRateRepository;
    @MockBean
    private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateService shippingRateService;

    @Test
    public void testCalculateShippingCost_NoRateFound(){
        Integer productId =1;
        Integer countryId= 14;
        String state = "ABCD";

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);

        assertThrows(ShippingRateNotFoundException.class, new Executable() {
            
            @Override
            public void execute() throws Throwable{
                shippingRateService.calculateShippingCost(productId, countryId, state);
            }
        });
    }

    @Test
    public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException{
        Integer productId =1;
        Integer countryId= 2;
        String state = "Alabama";

        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(10);

        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);

        Product product = new Product();
        product.setWeight(5);
        product.setWidth(4);
        product.setHeight(3);
        product.setLength(8);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

       float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

       assertEquals(50, shippingCost);
    }
    
}