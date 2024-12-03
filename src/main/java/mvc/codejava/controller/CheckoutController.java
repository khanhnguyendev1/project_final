package mvc.codejava.controller;

import mvc.codejava.entity.Coupon;
import mvc.codejava.entity.PaymentHistory;
import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.PurchaseItem;
import mvc.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        CartService cart = (CartService) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartService();
        }
        double totalPrice = cart.calculateTotal();

        if (totalPrice < 0.5) {
            throw new IllegalArgumentException("Total amount must be at least 0.5 USD for payment.");
        }
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", totalPrice);

        return "checkout";
    }

    @PostMapping("/checkout/complete")
    public String completeCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "couponCode", required = false) String couponCode,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

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

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            try {
                String orderId = UUID.randomUUID().toString();
                String successUrl = "http://localhost:8080/checkout/success";
                String cancelUrl = "http://localhost:8080/checkout/cancel";

                String paymentUrl = vnPayService.createPaymentUrl(totalPrice, orderId, successUrl);
                return "redirect:" + paymentUrl; // Chuyển hướng đến URL thanh toán
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Failed to initiate payment. Please try again.");
                return "redirect:/checkout";
            }
        } else if ("cash".equalsIgnoreCase(paymentMethod)) {
            // Xử lý thanh toán bằng tiền mặt
            PaymentHistory paymentHistory = new PaymentHistory();
            paymentHistory.setPaymentMethod(paymentMethod);
            paymentHistory.setTotalAmount(totalPrice);
            paymentHistory.setPaymentDate(new Date());
            paymentHistoryService.savePaymentHistory(paymentHistory);

            Purchase purchase = new Purchase();
            purchase.setOrderId(UUID.randomUUID().toString());
            purchase.setDate(new Date());
            purchase.setStatus("Pending");
            purchase.setPaymentHistory(paymentHistory);
            purchase.setCoupon(discount > 0 ? couponService.findByCode(couponCode) : null);

            List<PurchaseItem> purchaseItems = cart.getCartItems().entrySet().stream().map(entry -> {
                PurchaseItem purchaseItem = new PurchaseItem();
                purchaseItem.setProduct(entry.getKey());
                purchaseItem.setQuantity(entry.getValue());
                purchaseItem.setPurchase(purchase);
                return purchaseItem;
            }).collect(Collectors.toList());

            purchase.setPurchaseItems(purchaseItems);
            purchaseService.savePurchase(purchase);

            try {
                String customerEmail = purchase.getUser().getEmail();
                String subject = "Xác nhận đơn hàng - " + purchase.getOrderId();
                String text = "Cảm ơn bạn đã mua hàng! Đơn hàng của bạn đã được xác nhận. Mã đơn hàng của bạn là: " + purchase.getOrderId();
                emailService.sendEmail(customerEmail, subject, text);

                redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được xác nhận và email đã được gửi.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Có lỗi khi gửi email xác nhận.");
            }
            cart.clearCart();
            redirectAttributes.addFlashAttribute("message", "Thank you for your purchase! Your payment is completed.");
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid payment method.");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/checkout/success")
    public String paymentSuccess(@RequestParam Map<String, String> responseParams,
                                 Model model, RedirectAttributes redirectAttributes) {
        boolean isValid = vnPayService.validatePaymentResponse(responseParams);
        if (isValid) {
            String orderId = responseParams.get("vnp_TxnRef");
            Purchase purchase = purchaseService.findByOrderId(orderId);
            if (purchase != null) {
                purchase.setStatus("Completed");
                purchaseService.updatePurchase(purchase);
            }
            redirectAttributes.addFlashAttribute("message", "Payment successful! Thank you for your purchase.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment validation failed. Please contact support.");
        }
        return "redirect:/home";
    }

    @GetMapping("/checkout/cancel")
    public String paymentCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "You have cancelled the payment.");
        return "redirect:/checkout";
    }

    @PostMapping("/vnpay/checkout")
    @ResponseBody
    public ResponseEntity<?> createVnpayPayment(@RequestBody Map<String, Object> requestData) {
        try {
            double totalPrice = Double.parseDouble(requestData.get("totalPrice").toString());
            String orderId = UUID.randomUUID().toString();
            String returnUrl = "http://localhost:8080/checkout/success";
            String paymentUrl = vnPayService.createPaymentUrl(totalPrice, orderId, returnUrl);

            Map<String, String> response = new HashMap<>();
            response.put("url", paymentUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating VNPAY payment URL.");
        }
    }
}
