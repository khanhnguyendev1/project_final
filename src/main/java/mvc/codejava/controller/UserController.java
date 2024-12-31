package mvc.codejava.controller;
import mvc.codejava.entity.Role;
import mvc.codejava.entity.User;
import mvc.codejava.repository.RoleRepository;
import mvc.codejava.repository.UserRepository;
import mvc.codejava.service.CustomerService;
import mvc.codejava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại!");
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "edit-user";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật thông tin người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Người dùng đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa người dùng thất bại!" + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin-register";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, Model model) {
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(defaultRole);
        if (customerService.register(user) != null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("error", "Email đã tồn tại");
        return "admin-register";
    }
}
