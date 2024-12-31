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
        return "admin-dashboard";  // trả về view "admin-dashboard.html"
    }

    @GetMapping("/admin/user-orders/{userId}")
    public String viewUserOrders(@PathVariable Long userId, Model model) {
        // Lấy danh sách đơn hàng của người dùng
        List<Purchase> purchases = purchaseService.getAllPurchasesByUser(userId);

        // Thêm danh sách đơn hàng vào model để hiển thị trên giao diện
        model.addAttribute("purchases", purchases);
        return "user-orders"; // Tên file HTML
    }

    @GetMapping("/admin/all-orders")
    public String viewAllOrders(Model model) {
        List<Purchase> allPurchases = purchaseService.getAllPurchases();
        model.addAttribute("purchases", allPurchases);
        return "all-purchases";
    }

    @GetMapping("/admin/purchase/{id}")
    public String viewPurchaseDetails(@PathVariable Long id, Model model) {
        // Lấy thông tin chi tiết đơn hàng với id tương ứng
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));

        // Thêm đối tượng purchase vào model để hiển thị thông tin chi tiết
        model.addAttribute("purchase", purchase);

        return "purchase-details"; // Trang hiển thị chi tiết đơn hàng
    }

    @GetMapping("/admin/purchase/delete/{id}")
    public String deletePurchase(@PathVariable Long id) {
        // Tìm và xóa đơn hàng theo id
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));

        // Xóa tất cả PurchaseItem liên quan (được xóa tự động nếu cascade là ALL)
        purchaseRepository.delete(purchase);

        return "redirect:/admin/all-orders";  // Quay lại trang danh sách đơn hàng
    }
}
