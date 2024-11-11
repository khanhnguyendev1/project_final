package mvc.codejava.controller;

import mvc.codejava.entity.Product;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AppController {
	@RequestMapping("/")
	public String viewLandingPage(Model model) {
		return "landing";
	}

	@RequestMapping("/home")
	public String viewHomePage(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		// Đưa tên người dùng vào model
		model.addAttribute("email", username);
		return "home";
	}
}
