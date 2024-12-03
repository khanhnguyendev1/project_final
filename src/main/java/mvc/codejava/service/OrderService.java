package mvc.codejava.service;

import mvc.codejava.entity.*;
import mvc.codejava.repository.CouponRepository;
import mvc.codejava.repository.PaymentHistoryRepository;
import mvc.codejava.repository.PurchaseItemRepository;
import mvc.codejava.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    public void createOrder(List<Product> items, String paymentMethod, double totalPrice, String couponCode, String cardNumber) {
        // Tạo đối tượng Purchase mới
        Purchase purchase = new Purchase();
        purchase.setDate(new Date());
        purchase.setStatus("PENDING");  // Trạng thái ban đầu của đơn hàng

        // Xử lý mã giảm giá (Coupon)
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCode(couponCode);
            if (coupon != null && coupon.getExpiryDate().after(new Date())) {
                purchase.setCoupon(coupon);
                // Áp dụng giảm giá vào tổng giá
                double discount = totalPrice * (coupon.getDiscount() / 100);
            }
        }

        // Xử lý thanh toán thẻ tín dụng (PaymentHistory)
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPaymentMethod(paymentMethod);
        paymentHistory.setPaymentDate(new Date());

        // Lưu thông tin thanh toán trước
        paymentHistoryRepository.save(paymentHistory);

        // Gán PaymentHistory cho Purchase
        purchase.setPaymentHistory(paymentHistory);

        // Lưu thông tin PurchaseItem
        List<PurchaseItem> purchaseItems = new ArrayList<>();
        for (Product product : items) {
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(product);
            purchaseItem.setQuantity(1);  // Ví dụ đặt số lượng là 1, bạn có thể thay đổi theo giỏ hàng của người dùng
            purchaseItem.setPurchase(purchase);
            purchaseItems.add(purchaseItem);
        }

        // Gán danh sách PurchaseItem cho Purchase
        purchase.setPurchaseItems(purchaseItems);

        // Lưu Purchase vào cơ sở dữ liệu
        purchaseRepository.save(purchase);

        // Lưu từng PurchaseItem vào cơ sở dữ liệu
        for (PurchaseItem item : purchaseItems) {
            purchaseItemRepository.save(item);
        }
    }


}
