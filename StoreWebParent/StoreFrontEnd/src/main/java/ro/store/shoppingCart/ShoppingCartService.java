package ro.store.shoppingCart;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ro.store.common.entity.CartItem;
import ro.store.common.entity.Customer.Customer;
import ro.store.common.entity.Product.Product;
import ro.store.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

  private CartItemRepository cartItemRepository;
  private ProductRepository productRepository;

  public ShoppingCartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {

    Integer updatedQuantity = quantity;

    Product product = new Product(productId);
    CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

    if (cartItem != null) {
      updatedQuantity = cartItem.getQuantity() + quantity;
      if (updatedQuantity > 5) {
        throw new ShoppingCartException("Could not add more " + quantity + " item(s)" + " because there's already "
            + cartItem.getQuantity() + " items in your cart. Maximum quantity allowed is 5.");
      }
    } else {
      cartItem = new CartItem();
      cartItem.setCustomer(customer);
      cartItem.setProduct(product);
    }
    cartItem.setQuantity(updatedQuantity);
    cartItemRepository.save(cartItem);

    return updatedQuantity;
  }

  public List<CartItem> listCartItems(Customer customer) {
    return cartItemRepository.findByCustomer(customer);
  }

  public float updatedQuantity(Integer productId, Integer quantity, Customer customer) {
    cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
    Product product = productRepository.findById(productId).get();
    float subtotal = product.getDiscountPrice() * quantity;
    return subtotal;
  }

  public void removeProduct(Customer customer,Integer productId){
    cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
  }

}
