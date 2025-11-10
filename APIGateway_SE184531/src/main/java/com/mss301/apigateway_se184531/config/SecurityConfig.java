package com.mss301.apigateway_se184531.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Disable CSRF
                .csrf(csrf -> csrf.disable())
                // Enable CORS - Gateway doesn't handle it globally anymore
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOriginPatterns(java.util.Collections.singletonList("*"));
                    config.setAllowedMethods(
                            java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(java.util.Arrays.asList("Content-Type", "Authorization"));
                    config.setAllowCredentials(true);
                    config.setExposedHeaders(java.util.Arrays.asList("Authorization", "Content-Type"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                // Disable form login - this prevents login dialog
                .formLogin(form -> form.disable())
                // Disable HTTP Basic authentication - this prevents browser login prompt
                .httpBasic(basic -> basic.disable())
                // Configure authorization
                .authorizeExchange(exchange -> exchange
                        // Permit all Swagger and API documentation endpoints
                        .pathMatchers("/swagger-ui.html").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/v3/api-docs").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/swagger-resources").permitAll()
                        .pathMatchers("/swagger-resources/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers("/configuration/ui").permitAll()
                        .pathMatchers("/configuration/security").permitAll()
                        // Permit auth endpoints
                        .pathMatchers("/api/auth/**").permitAll()
                        // Permit public GET endpoints for blindboxes and categories
                        .pathMatchers("/api/blindboxes/**").permitAll()
                        .pathMatchers("/api/categories/**").permitAll()
                        // All other requests are allowed
                        .anyExchange().permitAll());

        return http.build();
    }
}
