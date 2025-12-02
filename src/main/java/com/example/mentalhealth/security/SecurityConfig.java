package com.example.mentalhealth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit login, register, H2 console, static resources
                        .requestMatchers("/login", "/register", "/h2-console/**", "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/progress/dashboard", true))
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

}
