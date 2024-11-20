package mvc.codejava.service;

import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.User;
import mvc.codejava.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchaseHistoryByUser(Optional<User> user) {
        return purchaseRepository.findByUser(user);
    }

    public Purchase findByOrderId(String orderId) {
        return purchaseRepository.findByOrderId(orderId);
    }

    public Purchase updatePurchase(Purchase updatedPurchase) {
        // Ensure the purchase exists before updating
        Optional<Purchase> existingPurchase = purchaseRepository.findById(updatedPurchase.getId());
        if (existingPurchase.isPresent()) {
            Purchase purchase = existingPurchase.get();
            purchase.setDate(updatedPurchase.getDate());
            purchase.setTotalPrice(updatedPurchase.getTotalPrice());
            purchase.setStatus(updatedPurchase.getStatus());
            purchase.setPaymentHistory(updatedPurchase.getPaymentHistory());
            purchase.setCoupon(updatedPurchase.getCoupon());
            purchase.setPurchaseItems(updatedPurchase.getPurchaseItems());
            return purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("Purchase not found with ID: " + updatedPurchase.getId());
        }
    }
}
