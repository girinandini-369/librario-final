package com.librario.config;

import com.librario.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomerUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // use CorsFilter bean
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public endpoints (no login needed)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/books/**").permitAll()
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers("/api/memberships/**").permitAll()
                        .requestMatchers("/api/borrow-records/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll() // 👈 register/login
                        .requestMatchers("/api/overdues/check").permitAll()
                        .requestMatchers("/api/transactions/**").permitAll()
                        .requestMatchers("/api/payments/**").permitAll() // 👈 keep payments open for Razorpay
                        .requestMatchers("/api/reminders/**").permitAll()


                        // 🔒 Role-protected endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/librarian/**").hasRole("LIBRARIAN")

                        // everything else requires login
                        .anyRequest().authenticated()
                )
                // For now just use HTTP Basic auth (simple testing). Later you can switch to JWT.
                .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500")); // frontend
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
