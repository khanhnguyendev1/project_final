package mvc.codejava.controller;

import mvc.codejava.entity.*;
import mvc.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showCheckoutPage(Model model) {
        // Hiển thị giỏ hàng và tổng tiền hiện tại
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "checkout";
    }
    

    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam("couponCode") String couponCode, Model model) {
        Coupon coupon = couponService.findByCode(couponCode);

        if (coupon == null || coupon.getExpiryDate().before(new Date())) {
            model.addAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        } else {
            cartService.applyDiscount(coupon);
            model.addAttribute("success", "Mã giảm giá đã được áp dụng.");
        }

        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "checkout";
    }

    @GetMapping("/complete")
    public String completeCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam("totalPrice") double totalPrice,
                                   @RequestParam(value = "couponCode", required = false) String couponCode,
                                   Model model) {

        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPaymentMethod(paymentMethod);
        paymentHistory.setTotalAmount(totalPrice);
        paymentHistory.setPaymentDate(new Date());
        paymentHistoryService.savePaymentHistory(paymentHistory);


        Coupon coupon = null;
        if (couponCode != null && !couponCode.isEmpty()) {
            coupon = couponService.findByCode(couponCode);
        }

        Purchase purchase = new Purchase();
        purchase.setDate(new Date());
        purchase.setStatus("COMPLETED");
        purchase.setTotalPrice(totalPrice);
        purchase.setPaymentHistory(paymentHistory);
        purchase.setCoupon(coupon);


        List<PurchaseItem> purchaseItems = new ArrayList<>();
        List<CartItem> cartItems = (List<CartItem>) cartService.getCartItems();

        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getQuantity() > 0) {
                    PurchaseItem purchaseItem = new PurchaseItem();
                    purchaseItem.setProduct(item.getProduct());
                    purchaseItem.setQuantity(item.getQuantity());
                    purchaseItem.setPurchase(purchase);
                    purchaseItems.add(purchaseItem);
                }
            }
        }

        purchase.setPurchaseItems(purchaseItems);
        purchaseService.savePurchase(purchase);

        model.addAttribute("message", "Thanh toán thành công!");
        return "checkout-success";
    }

    @GetMapping("/creditCard")
    public String creditCardForm(Model model, @RequestParam("totalPrice") String totalPrice, @RequestParam("couponCode") String couponCode) {
        // Truyền giá trị totalPrice và couponCode nếu cần
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("couponCode", couponCode);
        return "creditCardForm"; // Trả về trang nhập thẻ tín dụng
    }

//    @PostMapping("/checkout/submitCreditCardPayment")
//    public String submitCreditCardPayment(@RequestParam("cardNumber") String cardNumber,
//                                          @RequestParam("cardName") String cardName,
//                                          @RequestParam("expiryDate") String expiryDate,
//                                          @RequestParam("cvv") String cvv,
//                                          @RequestParam("totalPrice") String totalPrice,
//                                          @RequestParam("couponCode") String couponCode,
//                                          HttpSession session, Model model) {
//        // Logic xử lý thanh toán thẻ tín dụng tại đây
//        // Ví dụ: xác thực thông tin thẻ tín dụng, tích hợp cổng thanh toán...
//
//        CartService cart = (CartService) session.getAttribute("cart");
//        if (cart == null || cart.getCartItems().isEmpty()) {
//            model.addAttribute("error", "Giỏ hàng trống.");
//            return "cart";
//        }
//
//        // Sau khi thanh toán thành công
//        orderService.createOrder(cart.getCartItems(), "CreditCard", totalPrice, couponCode);
//        session.removeAttribute("cart");
//
//        return "checkout-success"; // Trang hiển thị thanh toán thành công
//    }
}
