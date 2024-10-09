package mvc.codejava.controller;

import java.util.List;

import mvc.codejava.repository.ProductRepository;
import mvc.codejava.service.ProductService;
import mvc.codejava.repository.UserRepository;
import mvc.codejava.entity.Product;
import mvc.codejava.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {
	@RequestMapping("/")
	public String viewHomePage(Model model) {
		return "index";
	}
}
