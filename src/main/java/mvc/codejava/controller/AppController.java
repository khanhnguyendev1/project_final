package mvc.codejava.controller;

import mvc.codejava.entity.Product;
import mvc.codejava.entity.User;
import mvc.codejava.repository.UserRepository;
import mvc.codejava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String viewLandingPage(Model model) {
		return "landing";
	}

	@RequestMapping("/user/profile")
	public String getUserProfile(Model model, Principal principal) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Optional<User> user = userRepository.findByEmail(username);
		model.addAttribute("user", user);
		model.addAttribute("fullName", user.get().getFullName());
		model.addAttribute("email",user.get().getEmail());
		model.addAttribute("phoneNumber", user.get().getPhoneNumber());
		model.addAttribute("address", user.get().getAddress());
		model.addAttribute("dateOfBirth", user.get().getDateOfBirth());
		return "profile";
	}
}
