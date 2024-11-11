package mvc.codejava.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.billingportal.Session;
import com.stripe.param.checkout.SessionCreateParams;
import mvc.codejava.entity.Coupon;
import mvc.codejava.entity.PaymentHistory;
import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.PurchaseItem;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {


    @Autowired
    private StripeService stripeService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) throws Exception {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        double totalPrice = cart.calculateTotal();
        String clientSecret = stripeService.createPaymentIntent(totalPrice);

        if (totalPrice < 0.5) {
            throw new IllegalArgumentException("Total amount must be at least 0.5 USD for payment.");
        }
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("stripePublicKey", "pk_test_51QFYEvJlZ89ABcRnBXRe2DmLy7nnWOWSK7V81qAYBodnIHrqNL8kh3EOf4H0AARdbfc10sFvss3HlMAzcJxMUtCw00AGErA6Ql");
        model.addAttribute("clientSecret", clientSecret);

        return "checkout";
    }

    @PostMapping("/checkout/complete")
    public String completeCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "couponCode", required = false) String couponCode,HttpSession session,
                                   Model model, RedirectAttributes redirectAttributes) {

        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        double discount = 0.0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponService.findByCode(couponCode);
            if (coupon != null) {
                discount = coupon.getDiscount();
                cart.applyDiscount(coupon);
            } else {
                redirectAttributes.addFlashAttribute("error", "Coupon code is invalid.");
                return "redirect:/checkout";
            }
        }


        double totalPrice = cart.getTotalPrice();
        if (totalPrice < 0.5) {
            redirectAttributes.addFlashAttribute("error", "Total amount must be at least 0.5 USD for payment.");
            return "redirect:/checkout";
        }


        String clientSecret;
        try {
            clientSecret = stripeService.createPaymentIntent(totalPrice);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to initiate payment. Please try again.");
            return "redirect:/checkout";
        }


        model.addAttribute("clientSecret", clientSecret);
        model.addAttribute("stripePublicKey", "pk_test_51QFYEvJlZ89ABcRnBXRe2DmLy7nnWOWSK7V81qAYBodnIHrqNL8kh3EOf4H0AARdbfc10sFvss3HlMAzcJxMUtCw00AGErA6Ql");
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", totalPrice);


        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPaymentMethod(paymentMethod);
        paymentHistory.setTotalAmount(totalPrice);
        paymentHistory.setPaymentDate(new java.util.Date());
        paymentHistoryService.savePaymentHistory(paymentHistory);


        Purchase purchase = new Purchase();
        purchase.setDate(new java.util.Date());
        purchase.setTotalPrice(totalPrice);
        purchase.setStatus("Completed");
        purchase.setPaymentHistory(paymentHistory);
        purchase.setCoupon(discount > 0 ? couponService.findByCode(couponCode) : null);

        // Lưu các sản phẩm từ giỏ hàng vào PurchaseItem và gán vào Purchase
        List<PurchaseItem> purchaseItems = cart.getCartItems().entrySet().stream().map(entry -> {
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(entry.getKey());
            purchaseItem.setQuantity(entry.getValue());
            purchaseItem.setPurchase(purchase);
            return purchaseItem;
        }).collect(Collectors.toList());

        purchase.setPurchaseItems(purchaseItems);
        purchaseService.savePurchase(purchase);


        cart.clearCart();

        redirectAttributes.addFlashAttribute("message", "Checkout successful! Thank you for your purchase.");
        return "redirect:/home";
    }

}
