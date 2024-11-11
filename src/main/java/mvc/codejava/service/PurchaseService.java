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
}
