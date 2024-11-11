package mvc.codejava.controller;

import mvc.codejava.entity.Purchase;
import mvc.codejava.entity.User;
import mvc.codejava.service.PurchaseService;
import mvc.codejava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/purchase-history")
public class PurchaseHistoryController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService; // Service để lấy thông tin người dùng hiện tại

    @GetMapping
    public String viewPurchaseHistory(Model model, Principal principal) {
        Optional<User> currentUser = userService.findByEmail(principal.getName()); // Lấy người dùng hiện tại
        List<Purchase> purchaseHistory = purchaseService.getPurchaseHistoryByUser(currentUser);
        model.addAttribute("purchaseHistory", purchaseHistory);
        return "purchase-history";
    }
}
