package mvc.codejava.controller;
import mvc.codejava.service.CartService;
import mvc.codejava.entity.Product;
import mvc.codejava.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping
    public String showCart(HttpSession session, Model model) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        model.addAttribute("total", cart.calculateTotal());
        model.addAttribute("cartItems", cart.getCartItems());
        return "cart";
    }

    @GetMapping("/add/{id}")
    public String addProductToCart(@PathVariable("id") Long productId, HttpSession session) {
        Product product = productService.findById(productId);
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        cart.addProduct(product);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
            session.setAttribute("cart", cart);
        }
        Product product = productService.findById(productId); // Lấy sản phẩm từ database
        cart.addProduct(product); // Thêm sản phẩm vào giỏ hàng
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeProductFromCart(@PathVariable("id") Long productId, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart != null) {
            Product product = productService.findById(productId);
            cart.removeProduct(product);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/increase/{id}")
    public String increaseQuantity(@PathVariable Long id, HttpSession session) {
        // Lấy giỏ hàng từ session
        CartService cart = (CartService) session.getAttribute("cart");

        // Nếu cart chưa tồn tại, khởi tạo một cart mới
        if (cart == null) {
            cart = new CartService(); // Giả sử bạn đã định nghĩa một lớp CartService
            session.setAttribute("cart", cart);
        }

        // Tìm sản phẩm theo ID
        Product product = productService.findById(id);
        if (product != null) {
            cart.addProduct(product); // Sử dụng CartService để thêm sản phẩm
        }

        return "redirect:/cart"; // Chuyển hướng lại trang giỏ hàng
    }

    @GetMapping("/decrease/{id}")
    public String decreaseQuantity(@PathVariable Long id, HttpSession session) {
        // Lấy giỏ hàng từ session
        CartService cart = (CartService) session.getAttribute("cart");

        if (cart != null) {
            // Tìm sản phẩm theo ID
            Product product = productService.findById(id);
            if (product != null) {
                cart.decreaseProductQuantity(product); // Sử dụng CartService để giảm số lượng hoặc xóa sản phẩm
            }
        }

        return "redirect:/cart"; // Chuyển hướng lại trang giỏ hàng
    }
}
