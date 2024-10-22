package mvc.codejava.service;
import mvc.codejava.entity.Coupon;
import mvc.codejava.entity.Product;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    private Map<Product, Integer> products = new HashMap<>();
    private Map<Product, Integer> cartItems = new HashMap<>();
    private double discount = 0.0;

    public Map<Product, Integer> getCartItems() {
        return cartItems;
    }

    public void addProduct(Product product) {
        products.put(product, products.getOrDefault(product, 0) + 1);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void decreaseProductQuantity(Product product) {
        int currentQuantity = products.getOrDefault(product, 0);
        if (currentQuantity > 1) {
            products.put(product, currentQuantity - 1);
        } else {
            products.remove(product); // Xóa sản phẩm nếu số lượng là 1
        }
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public double calculateTotal() {
        return products.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public double getTotalPrice() {
        double total = cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
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
