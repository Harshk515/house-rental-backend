package com.HouseRental.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 🔥 EXPLICIT FILTER BEAN
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ✅ ADD THIS LINE (IMPORTANT)
            .cors(cors -> {})

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth

                // public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/admin/login").permitAll()
                .requestMatchers("/api/properties/available").permitAll()
                .requestMatchers("/api/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()

                // tenant actions
                .requestMatchers(HttpMethod.POST, "/api/requests/**")
                .hasAuthority("ROLE_TENANT")
                .requestMatchers(HttpMethod.GET, "/api/requests/my-history")
                .hasAuthority("ROLE_TENANT")
                .requestMatchers(HttpMethod.PUT, "/api/requests/cancel/**")
                .hasAuthority("ROLE_TENANT")

                // landlord actions
                .requestMatchers(HttpMethod.GET, "/api/requests/**")
                .hasAuthority("ROLE_LANDLORD")
                .requestMatchers(HttpMethod.PUT, "/api/requests/**")
                .hasAuthority("ROLE_LANDLORD")

                // landlord-only property management
                .requestMatchers(HttpMethod.POST, "/api/properties/**")
                .hasAuthority("ROLE_LANDLORD")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**")
                .hasAuthority("ROLE_LANDLORD")
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**")
                .hasAuthority("ROLE_LANDLORD")

                // uploads
                .requestMatchers("/api/upload/**")
                .hasAuthority("ROLE_LANDLORD")

                // admin
             
                .requestMatchers("/api/admin/**")
                .hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()
            )

            // 🔥 FILTER IS NOW GUARANTEED TO RUN
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ ADD THIS BEAN (CRITICAL FOR BROWSER REQUESTS)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
}	
