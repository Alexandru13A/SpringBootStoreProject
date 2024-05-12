package ro.store.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.store.common.entity.CartItem;
import ro.store.common.entity.ShippingRate;
import ro.store.common.entity.product.Product;

@Service
public class CheckoutService {

  private static final int DIM_DIVISOR = 139;


  public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {

    CheckoutInfo checkoutInfo = new CheckoutInfo();

    float productCost = calculateProductCost(cartItems);
    float productTotal = calculateProductTotal(cartItems);
    float shippingCostTotal = calculateShippingCost(cartItems,shippingRate);
    float paymentTotalForOrder = productCost + shippingCostTotal;

    checkoutInfo.setProductCost(productCost);
    checkoutInfo.setProductTotal(productTotal);
    checkoutInfo.setDeliverDays(shippingRate.getDays());
    checkoutInfo.setCodSupported(shippingRate.isCodSupported());
    checkoutInfo.setShippingCostTotal(shippingCostTotal);
    checkoutInfo.setPaymentTotalForOrder(paymentTotalForOrder);

    return checkoutInfo;
  }

  private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {

    float shippingCostTotal = 0.0f;

    for (CartItem item : cartItems) {
      Product product = item.getProduct();
      float dimWeight= (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
      float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
      float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();

      item.setShippingCost(shippingCost);
      shippingCostTotal += shippingCost;
    }

    return shippingCostTotal;
  }

  private float calculateProductTotal(List<CartItem> cartItems) {
    float productTotal = 0.0f;

    for(CartItem item : cartItems){
      productTotal += item.getSubtotal();
    }
    return productTotal;
  }

  private float calculateProductCost(List<CartItem> cartItems) {
    float productCost = 0.0f;
    for (CartItem item : cartItems) {
      productCost += item.getQuantity() * item.getProduct().getCost();
    }
    return productCost;
  }

}
