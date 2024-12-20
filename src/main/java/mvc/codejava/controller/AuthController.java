package mvc.codejava.controller;

import mvc.codejava.entity.User;
import mvc.codejava.repository.RoleRepository;
import mvc.codejava.repository.UserRepository;
import mvc.codejava.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute User user, Model model) {
        if (customerService.register(user) != null) {
            return "redirect:/login";
        }
        model.addAttribute("error", "Email đã tồn tại");
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
