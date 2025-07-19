package com.arijit.job_management_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.arijit.job_management_api.filter.JwtFilter;

/**
 * MAIN SECURITY CONFIGURATION CLASS
 *
 * @Configuration - Tells Spring this class contains bean definitions
 * @EnableWebSecurity - Enables Spring Security's web security features and
 * filter chain
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT FILTER INJECTION
     *
     * @Autowired - Spring automatically injects the JwtFilter bean here This
     * filter will intercept requests to validate JWT tokens
     */
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * PASSWORD ENCODER BEAN CREATION
     *
     * @Bean - Creates a Spring-managed instance that can be injected elsewhere
     * BCryptPasswordEncoder - Industry standard for password hashing Strength
     * 12 - Makes hashing computationally expensive (security vs performance
     * trade-off) CRITICAL: Same encoder instance used for both registration and
     * authentication
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * AUTHENTICATION PROVIDER CONFIGURATION
     *
     * @Bean - Creates the main authentication provider for the application
     * DaoAuthenticationProvider - Uses database (DAO = Data Access Object) for
     * user lookup Parameters are automatically injected by Spring: -
     * UserDetailsService: Our UserDetailsServiceImpl (finds users in database)
     * - BCryptPasswordEncoder: Our password encoder bean (for password
     * verification)
     */
    @Bean
    public AuthenticationProvider authProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        // Create provider that uses database for user authentication
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // Set the same password encoder used during registration
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * MAIN SECURITY FILTER CHAIN CONFIGURATION
     *
     * @Bean - Creates the core security configuration HttpSecurity - Fluent API
     * for configuring web security This method defines: What requires
     * authentication, how authentication works, session management
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /**
                 * CSRF PROTECTION DISABLED CSRF = Cross-Site Request Forgery
                 * protection Disabled because: REST APIs are stateless and use
                 * tokens, not cookies customizer.disable() - Turns off CSRF
                 * protection
                 */
                .csrf(customizer -> customizer.disable())
                /**
                 * AUTHORIZATION RULES CONFIGURATION authorizeHttpRequests -
                 * Defines which URLs require authentication
                 */
                .authorizeHttpRequests(request -> request
                /**
                 * PUBLIC ENDPOINTS (NO AUTHENTICATION REQUIRED)
                 * requestMatchers("/register", "/login", "/logout") - Matches
                 * these exact URLs permitAll() - Allows access without
                 * authentication CRITICAL: This is why /register and /login
                 * work without authentication /logout is public to allow users
                 * to logout even with expired tokens
                 */
                .requestMatchers("/register", "/login", "/logout").permitAll()
                /**
                 * PROTECTED ENDPOINTS (AUTHENTICATION REQUIRED) anyRequest() -
                 * Matches all other URLs not specified above authenticated() -
                 * Requires user to be authenticated CRITICAL: This is why
                 * /allJobs requires login
                 */
                .anyRequest().authenticated()
                )
                /**
                 * HTTP BASIC AUTHENTICATION CONFIGURATION httpBasic() - Enables
                 * HTTP Basic Auth (username:password in Authorization header)
                 * Customizer.withDefaults() - Uses default Basic Auth settings
                 * Format: Authorization: Basic base64(username:password)
                 */
                .httpBasic(Customizer.withDefaults())
                /**
                 * SESSION MANAGEMENT CONFIGURATION sessionManagement() -
                 * Configures how sessions are handled
                 * SessionCreationPolicy.STATELESS - No server-side sessions
                 * stored CRITICAL: Each request must include authentication (no
                 * session cookies) Perfect for REST APIs and mobile apps
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /**
                 * JWT FILTER REGISTRATION addFilterBefore() - Adds our custom
                 * JWT filter to the security chain jwtFilter - Our custom
                 * filter that validates JWT tokens
                 * UsernamePasswordAuthenticationFilter.class - Run our filter
                 * before this built-in filter EXECUTION ORDER: JWT Filter →
                 * Basic Auth Filter → Controller
                 */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.disable());

        return http.build();
    }

    /**
     * AUTHENTICATION MANAGER BEAN
     *
     * @Bean - Creates AuthenticationManager for manual authentication Used by
     * UserController.login() to validate user credentials
     * AuthenticationConfiguration - Spring Security's configuration class
     * getAuthenticationManager() - Returns the configured authentication
     * manager WORKFLOW: UserController → AuthenticationManager →
     * UserDetailsServiceImpl → Database
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
