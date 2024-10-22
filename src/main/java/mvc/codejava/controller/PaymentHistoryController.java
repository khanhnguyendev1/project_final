package mvc.codejava.controller;

import mvc.codejava.entity.PaymentHistory;
import mvc.codejava.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/paymenthistories")
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @GetMapping
    public String listPaymentHistories(Model model) {
        model.addAttribute("paymentHistories", paymentHistoryService.getAllPaymentHistories());
        return "paymenthistory-list";
    }

    @GetMapping("/add")
    public String showAddPaymentHistoryForm(Model model) {
        model.addAttribute("paymentHistory", new PaymentHistory());
        return "paymenthistory-form";
    }

    @PostMapping("/save")
    public String savePaymentHistory(@ModelAttribute("paymentHistory") PaymentHistory paymentHistory) {
        paymentHistoryService.savePaymentHistory(paymentHistory);
        return "redirect:/paymenthistories";
    }

    @GetMapping("/edit/{id}")
    public String showEditPaymentHistoryForm(@PathVariable Long id, Model model) {
        PaymentHistory paymentHistory = paymentHistoryService.getPaymentHistoryById(id);
        model.addAttribute("paymentHistory", paymentHistory);
        return "paymenthistory-form";
    }

    @GetMapping("/delete/{id}")
    public String deletePaymentHistory(@PathVariable Long id) {
        paymentHistoryService.deletePaymentHistory(id);
        return "redirect:/paymenthistories";
    }
}
