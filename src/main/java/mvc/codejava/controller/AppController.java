package mvc.codejava.controller;

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
	public String viewHomePage(Model model) {return "home"; }
}
