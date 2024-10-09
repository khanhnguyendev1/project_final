package mvc.codejava.controller;

import mvc.codejava.entity.Customer;
import mvc.codejava.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute Customer customer, Model model) {
        // Kiểm tra email đã tồn tại chưa
        if (customerService.register(customer) != null) {
            return "redirect:/login"; // Chuyển tới trang đăng nhập sau khi đăng ký thành công
        }
        model.addAttribute("error", "Email đã tồn tại");
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
