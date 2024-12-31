package mvc.codejava.service;
import mvc.codejava.entity.Coupon;
import mvc.codejava.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CartService {
    private Map<Product, Integer> cartItems = new HashMap<>();
    private double discount = 0.0;

    public Map<Product, Integer> getCartItems() {
        return Collections.unmodifiableMap(cartItems);
    }

    public void addProduct(Product product) {
        cartItems.put(product, cartItems.getOrDefault(product, 0) + 1);
    }

    public void removeProduct(Product product) {
        cartItems.remove(product);
    }

    public void decreaseProductQuantity(Product product) {
        int currentQuantity = cartItems.getOrDefault(product, 0);
        if (currentQuantity > 1) {
            cartItems.put(product, currentQuantity - 1);
        } else {
            cartItems.remove(product);
        }
    }

    public double calculateTotal() {
        double total = cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        return Math.max(total, 0.5);
    }

    public double getTotalPrice() {
        double total = calculateTotal();
        return total - discount;
    }

    public void applyDiscount(Coupon coupon) {
        this.discount = coupon.getDiscount();
    }

    public void clearCart() {
        cartItems.clear();
        discount = 0.0;
    }
}
