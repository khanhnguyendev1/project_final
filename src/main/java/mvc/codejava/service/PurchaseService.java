package mvc.codejava.service;

import mvc.codejava.entity.Purchase;
import mvc.codejava.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
