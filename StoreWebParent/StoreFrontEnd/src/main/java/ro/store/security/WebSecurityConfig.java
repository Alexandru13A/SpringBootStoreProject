package ro.store.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ro.store.security.oauth2.CustomerOAuth2UserService;
import ro.store.security.oauth2.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private CustomerOAuth2UserService oauth2UserService;
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

	@Autowired
	public void setOauth2UserService(CustomerOAuth2UserService oauth2UserService) {
		this.oauth2UserService = oauth2UserService;
	}

	@Autowired
	@Lazy
	public void setOAuth2LoginSuccessHandler(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
	}

	@Autowired
	public void setDatabaseLoginSuccessHandler(DatabaseLoginSuccessHandler databaseLoginSuccessHandler) {
		this.databaseLoginSuccessHandler = databaseLoginSuccessHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				(requests) -> requests.requestMatchers("/css/**", "/images/**", "/js/**", "/webjars/**").permitAll()
						.requestMatchers("/account_details", "/update_account_details", "/cart", "/address_book/**")
						.authenticated().anyRequest().permitAll())
				.formLogin(login -> login.loginPage("/login")
						.usernameParameter("email")
						.successHandler(databaseLoginSuccessHandler)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2.loginPage("/login")
						.successHandler(oAuth2LoginSuccessHandler)
						.userInfoEndpoint()
						.userService(oauth2UserService))
				.logout(logout -> logout.permitAll())
				.rememberMe(
						remember -> remember.userDetailsService(userDetailsService()));

		return http.build();
	}

}