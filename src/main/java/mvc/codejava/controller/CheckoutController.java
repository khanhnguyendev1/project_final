package mvc.codejava.controller;

import jakarta.servlet.http.HttpSession;
import mvc.codejava.entity.*;
import mvc.codejava.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    private UserService userService;

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
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "address", required = false) String address,
                                   @RequestParam(value = "phone", required = false) String phone,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Tìm người dùng từ database
        Optional<User> optionalUser = userService.findByEmail(username);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
            return "redirect:/login";
        }
        User user = optionalUser.get();

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

        double totalPrice = cart.calculateTotal();

        Purchase purchase = new Purchase();
        purchase.setDate(new Date());
        purchase.setStatus("Pending");
        purchase.setCoupon(discount > 0 ? couponService.findByCode(couponCode) : null);
        purchase.setTotalPrice(totalPrice);
        purchase.setName(name);
        purchase.setAddress(address);
        purchase.setPhone(phone);
        purchase.setUser(user);

        purchaseService.savePurchase(purchase);

        if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            try {

                // Tạo URL thanh toán với id của purchase
                String successUrl = "http://localhost:8080/checkout/success";
                String cancelUrl = "http://localhost:8080/checkout/cancel";
                String paymentUrl = vnPayService.createPaymentUrl(totalPrice, purchase.getId().toString(), successUrl);
                return "redirect:" + paymentUrl;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Failed to initiate payment. Please try again.");
                return "redirect:/checkout";
            }
        } else if ("cash".equalsIgnoreCase(paymentMethod)) {
            // Xử lý thanh toán bằng tiền mặt
            PaymentHistory paymentHistory = new PaymentHistory();
            paymentHistory.setPaymentMethod(paymentMethod);
            paymentHistory.setTotalAmount(purchase.getTotalPrice());
            paymentHistory.setPaymentDate(new Date());
            paymentHistoryService.savePaymentHistory(paymentHistory);

            purchase.setStatus("Completed");
            purchase.setPaymentHistory(paymentHistory);
            purchase.setCoupon(discount > 0 ? couponService.findByCode(couponCode) : null);


            purchaseService.updatePurchase(purchase);

            List<PurchaseItem> purchaseItems = cart.getCartItems().entrySet().stream().map(entry -> {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();

                PurchaseItem purchaseItem = new PurchaseItem();
                purchaseItem.setProduct(product);
                purchaseItem.setProductName(product.getName()); // Lấy tên sản phẩm
                purchaseItem.setPriceAtPurchase(product.getPrice()); // Lấy giá sản phẩm tại thời điểm mua
                purchaseItem.setQuantity(quantity);
                purchaseItem.setPurchase(purchase);

                return purchaseItem;
            }).collect(Collectors.toList());

            purchase.setPurchaseItems(purchaseItems);
            purchaseService.updatePurchase(purchase);

            try {
                String customerEmail = purchase.getUser().getEmail();
                String subject = "Xác nhận đơn hàng - " + purchase.getId();
                String text = "Cảm ơn bạn đã mua hàng! Đơn hàng của bạn đã được xác nhận. Mã đơn hàng của bạn là: " + purchase.getId();
                emailService.sendEmail(customerEmail, subject, text);

                redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được xác nhận và email đã được gửi.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Có lỗi khi gửi email xác nhận.");
            }
            cart.clearCart();
            redirectAttributes.addFlashAttribute("message", "Thank you for your purchase! Your payment is completed.");
            return "redirect:/checkout/confirmation";
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
            Long purchaseId = Long.parseLong(responseParams.get("vnp_TxnRef")); // Parse the ID from the response
            Purchase purchase = purchaseService.findById(purchaseId);
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
            Long purchaseId = Long.parseLong(requestData.get("purchaseId").toString());  // Get purchaseId from request data
            Purchase purchase = purchaseService.findById(purchaseId);  // Fetch the purchase using the purchaseId
            String returnUrl = "http://localhost:8080/checkout/success";
            String paymentUrl = vnPayService.createPaymentUrl(totalPrice, String.valueOf(purchaseId), returnUrl);  // Use purchaseId

            Map<String, String> response = new HashMap<>();
            response.put("url", paymentUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating VNPAY payment URL.");
        }
    }

    @GetMapping("/checkout/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Bạn đã đặt hàng thành công! Chúng tôi sẽ kiểm tra và xử lý đơn hàng của bạn.");
        return "order-confirmation";
    }
}
