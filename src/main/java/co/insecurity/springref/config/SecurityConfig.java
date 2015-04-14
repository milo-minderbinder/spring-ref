package co.insecurity.springref.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.insecurity.policy.PasswordPolicy;
import co.insecurity.springref.core.domain.UserRole;
import co.insecurity.springref.event.users.CreateUserEvent;
import co.insecurity.springref.event.users.UserInfo;
import co.insecurity.springref.persistence.service.UserPersistenceService;
import co.insecurity.springref.security.RateLimitingDaoAuthenticationProvider;
import co.insecurity.springref.security.policy.SimplePasswordPolicy;


@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	private UserPersistenceService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, 
			@Value("${admin.username}") String adminUsername,
			@Value("${admin.password}") String adminPassword)
					throws Exception {
		auth.authenticationProvider(authenticationProvider);
		UserInfo adminUser = new UserInfo(
				adminUsername, 
				passwordEncoder.encode(adminPassword));
		adminUser.getRoles().add(UserRole.ADMIN);
		userService.createUser(new CreateUserEvent(adminUser));
		LOG.debug("Added default admin user: {}", adminUser);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.requiresChannel()
				.anyRequest()
					.requiresSecure()
				.and()
			.authorizeRequests()
				.antMatchers("/admin/**")
					.hasRole(UserRole.ADMIN.toString())
				.antMatchers("/user/**")
					.hasRole(UserRole.USER.toString())
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll()
				.and()
			.sessionManagement()
				.maximumSessions(1)
				.expiredUrl("/login?logout")
				.and()
				.invalidSessionUrl("/login?logout");
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public PasswordPolicy getPasswordPolicy() {
		return new SimplePasswordPolicy();
	}
	
	@Bean
	public AuthenticationProvider getAuthenticationProvider(
			@Value("${rateLimitingAuthenticationProvider.attemptsPerInterval}") int attemptsPerInterval,
			@Value("${rateLimitingAuthenticationProvider.rateLimit}") double rateLimit) {
		RateLimitingDaoAuthenticationProvider authProvider = 
				new RateLimitingDaoAuthenticationProvider(attemptsPerInterval, rateLimit);
		authProvider.setPasswordEncoder(passwordEncoder);
		authProvider.setUserDetailsService(userService);
		return authProvider;
	}
}
