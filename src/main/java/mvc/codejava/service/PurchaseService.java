package mvc.codejava.service;

import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.User;
import mvc.codejava.repository.PurchaseRepository;
import mvc.codejava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchaseHistoryByUser(Optional<User> user) {
        return purchaseRepository.findByUser(user);
    }

    public Purchase updatePurchase(Purchase updatedPurchase) {
        // Ensure the purchase exists before updating
        Optional<Purchase> existingPurchase = purchaseRepository.findById(updatedPurchase.getId());
        if (existingPurchase.isPresent()) {
            Purchase purchase = existingPurchase.get();
            purchase.setDate(updatedPurchase.getDate());
            purchase.setStatus(updatedPurchase.getStatus());
            purchase.setPaymentHistory(updatedPurchase.getPaymentHistory());
            purchase.setCoupon(updatedPurchase.getCoupon());
            purchase.setPurchaseItems(updatedPurchase.getPurchaseItems());
            return purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("Purchase not found with ID: " + updatedPurchase.getId());
        }
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> getAllPurchasesByUser(Long userId) {
        // Tìm người dùng theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Trả về danh sách đơn hàng của người dùng
        return purchaseRepository.findByUser(Optional.ofNullable(user));
    }
}
