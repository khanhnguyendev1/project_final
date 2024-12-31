package mvc.codejava.controller;
import jakarta.servlet.http.HttpSession;
import mvc.codejava.entity.CartItem;
import mvc.codejava.service.CartService;
import mvc.codejava.entity.Product;
import mvc.codejava.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cartItems")
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
        Product product = productService.findById(productId);
        cart.addProduct(product);
        session.setAttribute("cart", cart);
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
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
            session.setAttribute("cart", cart);
        }

        Product product = productService.findById(id);
        if (product != null) {
            cart.addProduct(product);
        }

        return "redirect:/cart";
    }

    @GetMapping("/decrease/{id}")
    public String decreaseQuantity(@PathVariable Long id, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");

        if (cart != null) {

            Product product = productService.findById(id);
            if (product != null) {
                cart.decreaseProductQuantity(product);
            }
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart/checkout")
    public String goToCheckoutPage(Model model) {
        List<CartItem> cartItems = (List<CartItem>) cartService.getCartItems();

        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống");
            return "cart"; // Quay lại giỏ hàng nếu không có sản phẩm
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", cartService.calculateTotal());

        return "checkout"; // Tới trang thanh toán
    }
}
