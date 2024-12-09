package mvc.codejava;

import mvc.codejava.repository.RoleRepository;
import mvc.codejava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProductManagerApplication extends SpringBootServletInitializer {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ProductManagerApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		// Khởi tạo các vai trò
//		if (roleRepository.findByName("ADMIN") == null) {
//			Role adminRole = new Role();
//			adminRole.setName("ADMIN");
//			roleRepository.save(adminRole);
//		}
//
//		if (roleRepository.findByName("USER") == null) {
//			Role userRole = new Role();
//			userRole.setName("USER");
//			roleRepository.save(userRole);
//		}
//
//		if (roleRepository.findByName("CUSTOMER") == null) {
//			Role customerRole = new Role();
//			customerRole.setName("CUSTOMER");
//			roleRepository.save(customerRole);
//		}
//
//		// Tạo người dùng Admin
//		User user = new User();
//		user.setEmail("admin@gmail.com");
//		user.setPassword(passwordEncoder.encode("password"));
//
//
//		Role role = roleRepository.findByName("ADMIN");
//
//		UserRole userRole = new UserRole();
//		userRole.setUser(user);
//		userRole.setRole(role);
//
//		// Khởi tạo danh sách userRoles nếu nó null
//		if (user.getUserRoles() == null) {
//			user.setUserRoles(new ArrayList<>());
//		}
//
//		user.getUserRoles().add(userRole); // Thêm vai trò vào danh sách
//
//		// Lưu người dùng vào cơ sở dữ liệu
//		userRepository.save(user);
//	}
}
