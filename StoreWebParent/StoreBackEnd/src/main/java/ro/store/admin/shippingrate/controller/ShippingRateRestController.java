package ro.store.admin.shippingrate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.shippingrate.ShippingRateService;
import ro.store.common.exception.rate.ShippingRateNotFoundException;

@RestController
public class ShippingRateRestController {

    private ShippingRateService shippingRateService;

    public ShippingRateRestController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException {

        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
        return String.valueOf(shippingCost);

    }

}
