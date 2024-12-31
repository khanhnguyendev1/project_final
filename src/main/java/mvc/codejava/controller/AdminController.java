package mvc.codejava.controller;

import mvc.codejava.entity.Purchase;
import mvc.codejava.repository.PurchaseRepository;
import mvc.codejava.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseRepository purchaseRepository;
    @GetMapping("/admin/dashboard")
    public String showDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/user-orders/{userId}")
    public String viewUserOrders(@PathVariable Long userId, Model model) {
        List<Purchase> purchases = purchaseService.getAllPurchasesByUser(userId);
        model.addAttribute("purchases", purchases);
        return "user-orders";
    }

    @GetMapping("/admin/all-orders")
    public String viewAllOrders(Model model) {
        List<Purchase> allPurchases = purchaseService.getAllPurchases();
        model.addAttribute("purchases", allPurchases);
        return "all-purchases";
    }

    @GetMapping("/admin/purchase/{id}")
    public String viewPurchaseDetails(@PathVariable Long id, Model model) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        model.addAttribute("purchase", purchase);

        return "purchase-details";
    }

    @GetMapping("/admin/purchase/delete/{id}")
    public String deletePurchase(@PathVariable Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        purchaseRepository.delete(purchase);

        return "redirect:/admin/all-orders";
    }
}
