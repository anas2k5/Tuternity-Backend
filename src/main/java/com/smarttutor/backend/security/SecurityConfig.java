package com.smarttutor.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        // 🛑 CRITICAL FIX: Explicitly allow POST for registration (public access) 🛑
                        // This prevents the request from being blocked, even if the general /api/auth/** rule is overridden.
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        // General Auth endpoints (including login)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Teacher List Viewing ( public access for browsing)
                        .requestMatchers(HttpMethod.GET, "/api/teacher/**").permitAll()

                        // 🛑 TEACHER MANAGEMENT RULES 🛑
                        .requestMatchers(HttpMethod.GET, "/api/teacher/profile").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/teacher/profile").hasAuthority("ROLE_TEACHER")

                        // Availability Management
                        .requestMatchers(HttpMethod.POST, "/api/availability/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/availability/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.GET, "/api/availability/teacher").hasAuthority("ROLE_TEACHER")

                        // Teacher creation/update (general POST, secured)
                        .requestMatchers(HttpMethod.POST, "/api/teacher/**").hasAuthority("ROLE_TEACHER")


                        // Booking/Student Rules
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/student/**").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").hasAuthority("ROLE_STUDENT")

                        // Admin-only routes
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // All others must be authenticated
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}