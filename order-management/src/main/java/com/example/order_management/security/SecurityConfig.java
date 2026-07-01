package com.example.order_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withIssuerLocation("http://localhost:9090/realms/eai-realm").build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoint publik (Bisa diakses tanpa token)
                        .requestMatchers("/api/products/**", "/api/orders","/api/order", "/orders", "/api/orders/**", "/payment/**", "/shipping/**").permitAll()
                        // Semua endpoint lain wajib punya token valid
                        .anyRequest().authenticated()
                )
                // Memberitahu Spring untuk menggunakan JwtDecoder yang kita buat di atas
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}