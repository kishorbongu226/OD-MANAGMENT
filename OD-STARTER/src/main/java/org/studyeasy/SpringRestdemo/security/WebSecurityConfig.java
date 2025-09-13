package org.studyeasy.SpringRestdemo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.studyeasy.SpringRestdemo.service.AccountService;
import org.studyeasy.SpringRestdemo.service.ProfessorService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    private final AccountService accountService; // inject your custom service
    private final ProfessorService professorService; // inject your custom service

    public WebSecurityConfig(AccountService accountService,ProfessorService professorService) {
        this.accountService = accountService;
        this.professorService = professorService;
    }

    // ✅ Tell Spring to use your custom UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return accountService;
    }

    // ✅ Expose AuthenticationManager so Spring can authenticate users
@Bean
public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(daoAuthProvider(accountService))
            .authenticationProvider(daoAuthProvider(professorService))
            .build();
}

private DaoAuthenticationProvider daoAuthProvider(UserDetailsService userDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
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
                    auth.requestMatchers("/admin/**").hasAuthority("ADMIN");
                    auth.requestMatchers("/api/v1/**").hasAuthority("ADMIN");
                    auth.requestMatchers("api/v1/teacher/**").hasAuthority("TEACHER");
                    auth.requestMatchers("/api/v1/student/**").hasAuthority("STUDENT");
                })
                .formLogin(form -> {
                    log.info("Custom form login configuration enabled");
                    form.loginPage("/login")
                            .successHandler((request, response, authentication) -> {
                                String username = authentication.getName();
                                String role = authentication.getAuthorities().iterator().next().getAuthority();

                                log.info("Login success for user: {} with role: {}", username, role);

                                if ("ADMIN".equals(role)) {
                                    response.sendRedirect("/api/v1/events/add");
                                } else if ("TEACHER".equals(role)) {
                                    response.sendRedirect("/api/v1/teacher/event/status");
                                } else if ("STUDENT".equals(role)) {
                                    response.sendRedirect("/api/v1/student/profile");
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
