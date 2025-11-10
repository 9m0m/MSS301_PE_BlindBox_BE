package com.mss301.msblindbox_se184531.config;

import com.mss301.msblindbox_se184531.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow GET requests without authentication
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/blindboxes",
                                "/api/blindboxes/**")
                        .permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories",
                                "/api/categories/**")
                        .permitAll()
                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Component
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        @Autowired
        private JwtUtil jwtUtil;

        @Value("${jwt.admin-role-value:1}")
        private Integer adminRoleValue;

        @Value("${jwt.admin-authority-name:ROLE_ADMINISTRATOR}")
        private String adminAuthorityName;

        @Value("${jwt.customer-authority-name:ROLE_CUSTOMER}")
        private String customerAuthorityName;

        @Value("${jwt.require-active:true}")
        private boolean requireActive;

        @Value("${jwt.active-claim:isActive}")
        private String activeClaimName;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    if (requireActive) {
                        Boolean isActive = jwtUtil.getBooleanClaim(token, activeClaimName);
                        if (isActive == null || !isActive) {
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                    Integer role = jwtUtil.getRoleFromToken(token);
                    String roleName = role != null && role.equals(adminRoleValue) ? adminAuthorityName
                            : customerAuthorityName;

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            "user", null, Collections.singletonList(new SimpleGrantedAuthority(roleName)));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
