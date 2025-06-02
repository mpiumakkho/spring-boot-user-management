package com.mp.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mp.web.security.CoreApiAuthProvider;
import com.mp.web.security.SessionAuthSuccessHandler;
import com.mp.web.security.SessionFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CoreApiAuthProvider coreApiAuthProvider;

    @Autowired
    private SessionAuthSuccessHandler sessionAuthSuccessHandler;
    
    @Autowired
    private SessionFilter sessionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(coreApiAuthProvider)
            .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/auth/login", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // admin area
                .requestMatchers("/admin/**").hasRole("SUPER_ADMIN")
                // user management
                .requestMatchers("/users/create", "/users/edit/**", "/users/update", "/users/delete/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "USER_MANAGER")
                .requestMatchers("/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "USER_MANAGER", "MODERATOR", "VIEWER", "SUPPORT")
                // role management
                .requestMatchers("/roles/create", "/roles/edit/**", "/roles/update", "/roles/delete/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/roles/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "VIEWER")
                // permission management
                .requestMatchers("/permissions/create", "/permissions/edit/**", "/permissions/update", "/permissions/delete/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/permissions/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "VIEWER")
                // reports
                .requestMatchers("/reports/export", "/reports/analytics").hasAnyRole("SUPER_ADMIN", "ANALYST")
                .requestMatchers("/reports/**").hasAnyRole("SUPER_ADMIN", "ANALYST", "VIEWER", "SUPPORT")
                // dashboard and profile
                .requestMatchers("/dashboard", "/profile/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/")
                .loginProcessingUrl("/")
                .successHandler(sessionAuthSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public OncePerRequestFilter loggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, java.io.IOException {
                // logger.info("[SECURITY] " + request.getMethod() + " " + request.getRequestURI());
                filterChain.doFilter(request, response);
            }
        };
    }
} 