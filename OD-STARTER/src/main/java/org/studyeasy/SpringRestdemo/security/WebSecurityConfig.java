package org.studyeasy.SpringRestdemo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.studyeasy.SpringRestdemo.service.AccountService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    private final AccountService accountService; // inject your custom service

    public WebSecurityConfig(AccountService accountService) {
        this.accountService = accountService;
    }

    // ✅ Tell Spring to use your custom UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return accountService;
    }

    // ✅ Expose AuthenticationManager so Spring can authenticate users
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    private static final String[] WHITELIST = {
            "/login", "/db-console/**",
            "/css/**", "/fonts/**", "/images/**", "/js/**", "/resources/**", "/error/**"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        log.info("PasswordEncoder (BCrypt) bean initialized");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring SecurityFilterChain...");

        http
                .authorizeHttpRequests(auth -> {
                    log.info("Setting up request authorization rules...");
                    auth.requestMatchers(WHITELIST).permitAll();
                    auth.requestMatchers("/profile/**").authenticated();
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/teacher/**").hasRole("TEACHER");
                    auth.requestMatchers("/student/**").hasRole("STUDENT");
                })
                .formLogin(form -> {
                    log.info("Custom form login configuration enabled");
                    form.loginPage("/login")
                            .successHandler((request, response, authentication) -> {
                                String username = authentication.getName();
                                String role = authentication.getAuthorities().iterator().next().getAuthority();

                                log.info("Login success for user: {} with role: {}", username, role);

                                if ("ROLE_ADMIN".equals(role)) {
                                    response.sendRedirect("/admin/dashboard");
                                } else if ("ROLE_TEACHER".equals(role)) {
                                    response.sendRedirect("/teacher/dashboard");
                                } else if ("ROLE_STUDENT".equals(role)) {
                                    response.sendRedirect("/student/dashboard");
                                } else {
                                    log.warn("Unknown role for user {}: {}", username, role);
                                    response.sendRedirect("/login?errorcdbschdbcdl");
                                }
                            }).failureHandler((request, response, exception) -> {
                                String username = request.getParameter(""); // or "email" if you renamed it
                                log.error("Login failed for user {}: {}", username, exception.getMessage());
                                request.getParameterMap().forEach(
                                        (key, value) -> log.info("Param {} = {}", key, String.join(",", value)));

                                // You can redirect with a custom query param to show a message
                                response.sendRedirect("/login?error=true");
                            })
                            .permitAll();
                })
                .logout(logout -> {
                    log.info("Logout configuration enabled");
                    logout.logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .addLogoutHandler((request, response, authentication) -> {
                                if (authentication != null) {
                                    log.info("User {} logged out successfully", authentication.getName());
                                }
                            })
                            .permitAll();
                })
                .csrf(csrf -> {
                    log.info("Disabling CSRF protection (make sure this is safe for your app!)");
                    csrf.disable();
                })
                .headers(headers -> {
                    log.info("Disabling frame options (for H2 console, etc.)");
                    headers.frameOptions(frame -> frame.disable());
                });

        log.info("SecurityFilterChain built successfully");
        return http.build();
    }
}
