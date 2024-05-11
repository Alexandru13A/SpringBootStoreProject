package ro.store.admin.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public UserDetailsService userDetailsService() {
		return new StoreUserDetailsService();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests) -> requests
		    .requestMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
				.requestMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
				.requestMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
				.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
				.requestMatchers("/products", "/products/", "products/detail/**", "/products/page/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
				.requestMatchers("/products/edit/**", "/products/save", "/products/check_unique").hasAnyAuthority("Admin", "Salesperson", "Editor")
				.requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
				.requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
				.requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
				.requestMatchers("/customers/**", "/orders/**", "/get_shipping_cost").hasAnyAuthority("Admin", "Salesperson")
				.requestMatchers("/images/**", "/js/**", "/webjars/**")
				.permitAll()
				.anyRequest()
				.authenticated())
				.formLogin((form) -> form
						.loginPage("/login")
						.usernameParameter("email")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll());

		http
				.headers()
				.contentSecurityPolicy("frame-ancestors 'self'");

		return http.build();
	}

}