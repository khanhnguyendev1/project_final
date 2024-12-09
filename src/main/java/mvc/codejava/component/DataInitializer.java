//package mvc.codejava.component;
//
//import mvc.codejava.entity.Role;
//import mvc.codejava.entity.User;
//import mvc.codejava.repository.RoleRepository;
//import mvc.codejava.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            Role adminRole = new Role();
//            adminRole.setName("ROLE_ADMIN");
//            roleRepository.save(adminRole);
//
//            Role userRole = new Role();
//            userRole.setName("ROLE_USER");
//            roleRepository.save(userRole);
//
//            Optional<User> adminUserOpt = userRepository.findByEmail("admin1@gmail.com");
//            if (!adminUserOpt.isPresent()) {
//                User admin = new User();
//                admin.setAddress("43 Le Huu Trac");
//                admin.setDateOfBirth("03/27/2002");
//                admin.setEmail("admin1@gmail.com");
//                admin.setFullName("Admin");
//                admin.setPassword(passwordEncoder.encode("admin123"));
//                admin.setPhoneNumber("0905188409");
//                admin.setRegistrationDate(LocalDate.now());
//                admin.setRoles(Set.of(adminRole));
//                userRepository.save(admin);
//            }
//
//
//            Optional<User> normalUserOpt = userRepository.findByEmail("user@gmail.com");
//            if (!normalUserOpt.isPresent()) {
//                User user = new User();
//                user.setAddress("35 Le Huu Trac");
//                user.setDateOfBirth("02/27/2002");
//                user.setEmail("user@gmail.com");
//                user.setFullName("User");
//                user.setPassword(passwordEncoder.encode("user123"));
//                user.setPhoneNumber("0905188410");
//                user.setRegistrationDate(LocalDate.now());
//                user.setRoles(Set.of(userRole));
//                userRepository.save(user);
//            }
//        } catch (Exception e) {
//            // Log lỗi chi tiết để dễ dàng debug
//            System.err.println("Error initializing data: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
