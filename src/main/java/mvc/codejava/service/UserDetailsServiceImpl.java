package mvc.codejava.service;

import mvc.codejava.entity.Customer;
import mvc.codejava.entity.User;
import mvc.codejava.repository.CustomerRepository;
import mvc.codejava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

		return new org.springframework.security.core.userdetails.User(
				customer.getEmail(),
				customer.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

}
