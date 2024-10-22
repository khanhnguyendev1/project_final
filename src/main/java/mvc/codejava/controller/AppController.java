package mvc.codejava.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {
	@RequestMapping("/")
	public String viewLandingPage(Model model) {
		return "landing";
	}

	@RequestMapping("/home")
	public String viewHomePage(Model model)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		// Đưa tên người dùng vào model
		model.addAttribute("email", username);
		return "home";
	}
}
