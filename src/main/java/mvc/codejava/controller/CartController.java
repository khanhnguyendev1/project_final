package mvc.codejava.controller;
import mvc.codejava.service.CartService;
import mvc.codejava.entity.Product;
import mvc.codejava.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showCart(HttpSession session, Model model) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        model.addAttribute("products", cart.getProducts());
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
        cart.addProduct(product);  // Tăng số lượng sản phẩm nếu đã có trong giỏ
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
    public String increaseProductQuantity(@PathVariable("id") Long productId, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart != null) {
            Product product = productService.findById(productId);
            cart.addProduct(product); // Tăng số lượng sản phẩm
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/decrease/{id}")
    public String decreaseProductQuantity(@PathVariable("id") Long productId, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart != null) {
            Product product = productService.findById(productId);
            cart.decreaseProductQuantity(product); // Giảm số lượng sản phẩm
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
}
