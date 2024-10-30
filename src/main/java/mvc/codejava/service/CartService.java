package mvc.codejava.service;
import mvc.codejava.entity.Coupon;
import mvc.codejava.entity.Product;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    private Map<Product, Integer> cartItems = new HashMap<>(); // Giỏ hàng lưu trữ sản phẩm và số lượng
    private double discount = 0.0;

    // Phương thức để lấy các sản phẩm trong giỏ hàng
    public Map<Product, Integer> getCartItems() {
        return cartItems;
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addProduct(Product product) {
        cartItems.put(product, cartItems.getOrDefault(product, 0) + 1); // Tăng số lượng sản phẩm
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProduct(Product product) {
        cartItems.remove(product); // Xóa sản phẩm khỏi giỏ hàng
    }

    // Giảm số lượng sản phẩm trong giỏ hàng
    public void decreaseProductQuantity(Product product) {
        int currentQuantity = cartItems.getOrDefault(product, 0);
        if (currentQuantity > 1) {
            cartItems.put(product, currentQuantity - 1); // Giảm số lượng
        } else {
            cartItems.remove(product); // Xóa sản phẩm nếu số lượng là 1
        }
    }

    // Tính tổng giá trị của giỏ hàng
    public double calculateTotal() {
        return cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    // Tính tổng giá trị với giảm giá
    public double getTotalPrice() {
        double total = calculateTotal(); // Tính tổng từ cartItems
        return total - discount; // Trừ đi giảm giá
    }

    // Áp dụng mã giảm giá
    public void applyDiscount(Coupon coupon) {
        this.discount = coupon.getDiscount(); // Lưu giá trị giảm giá
    }

    // Xóa tất cả sản phẩm trong giỏ hàng
    public void clearCart() {
        cartItems.clear(); // Xóa giỏ hàng
        discount = 0.0; // Đặt lại giảm giá
    }
}
