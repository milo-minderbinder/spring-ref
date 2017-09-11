package co.insecurity.springref.config;

import co.insecurity.policy.PasswordPolicy;
import co.insecurity.springref.core.domain.UserRole;
import co.insecurity.springref.event.users.CreateUserEvent;
import co.insecurity.springref.event.users.UserInfo;
import co.insecurity.springref.persistence.service.UserPersistenceService;
import co.insecurity.springref.security.RateLimitingDaoAuthenticationProvider;
import co.insecurity.springref.security.policy.SimplePasswordPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@Import(ServiceConfig.class)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${rateLimitingAuthenticationProvider.attemptsPerInterval}")
    private int authNAttemptsPerInterval;

    @Value("${rateLimitingAuthenticationProvider.rateLimit}")
    private double authNRateLimit;

    private final UserPersistenceService userService;

    public SecurityConfig(UserPersistenceService userService) {
        this.userService = userService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                @Value("${admin.username}") String adminUsername,
                                @Value("${admin.password}") String adminPassword)
            throws Exception {
        auth.authenticationProvider(getAuthenticationProvider());
        UserInfo adminUser = new UserInfo(
                adminUsername,
                getPasswordEncoder().encode(adminPassword));
        adminUser.getRoles().add(UserRole.ADMIN);
        userService.createUser(new CreateUserEvent(adminUser));
        LOG.debug("Added default admin user: {}", adminUser);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // While configurable at the app container or server level, configuring channel security in the
                // code itself can ensure that deployment environment issues, like failure to enforce HTTPS site-wide,
                // will not affect the application's security and will be more easily identified.
                .requiresChannel()
                    .anyRequest()
                        .requiresSecure()
                    .and()
                .authorizeRequests()
                    .antMatchers("/admin/**")
                        .hasRole(UserRole.ADMIN.toString())
                    .antMatchers("/user/**")
                        .hasRole(UserRole.USER.toString())
                    .antMatchers("/", "/register", "/resources/**")
                        .permitAll()
                    // Adding a catch-all pattern to enforce authentication for any remaining unspecified paths is an
                    // effective way to ensure that security controls "fail closed" and preventing access to functionality
                    // and paths that are not explicitly exposed to users/roles
                    .anyRequest()
                        .authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                // By default, sets logout URL to `/logout`, logout success URL to `/login?logout`, and invalidates the
                // session on logout
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

    /**
     * Get the {@link PasswordEncoder} bean, which can be used to safely hash credentials for storage.
     *
     * Spring Security provides the {@link BCryptPasswordEncoder}, which uses the bcrypt one-way hashing algorithm for
     * hashing passwords and other credentials. Bcrypt offers security improvements over other hashing functions, like
     * SHA and even PBKDF2, because passwords hashed with bcrypt are more resistant to rainbow-table attacks, since the
     * algorithm is intentionally CPU and GPU intensive, making it much more resource/time intensive for an attacker to
     * generate password hashes from a dictionary.
     *
     * @return the {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Get the {@link SimplePasswordPolicy} bean, which can be used to verify that passwords meet policy requirements.
     *
     * In this application, the {@link SimplePasswordPolicy} defines example length, character, and uniqueness policy
     * requirements, and is used by the {@link co.insecurity.springref.web.domain.UserValidator} during registration and
     * password change/update flows as a centralized point of enforcement for these policies.
     *
     * @return the {@link SimplePasswordPolicy}
     */
    @Bean
    public PasswordPolicy getPasswordPolicy() {
        return new SimplePasswordPolicy(true);
    }

    /**
     * Get the {@link AuthenticationProvider} bean, used by the {@link AuthenticationManager} to process a specific
     * {@link org.springframework.security.core.Authentication Authentication} implementation.
     *
     * In this configuration, a {@link RateLimitingDaoAuthenticationProvider} is instantiated, which limits the rate at
     * which a particular username can be used to authenticate successfully from a given IP address. In this case, the
     * {@link RateLimitingDaoAuthenticationProvider} uses the configured {@link BCryptPasswordEncoder} to hash passwords,
     * and a {@link UserPersistenceService} to provide simple CRUD operations using JDBC to retrieve user information.
     *
     * @return the {@link RateLimitingDaoAuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider getAuthenticationProvider() {
        RateLimitingDaoAuthenticationProvider authProvider =
                new RateLimitingDaoAuthenticationProvider(authNAttemptsPerInterval, authNRateLimit);
        authProvider.setPasswordEncoder(getPasswordEncoder());
        authProvider.setUserDetailsService(userService);
        return authProvider;
    }
}
