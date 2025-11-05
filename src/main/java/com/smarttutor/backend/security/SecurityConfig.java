package com.smarttutor.backend.security;

import lombok.RequiredArgsConstructor;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ Allow everyone (students, teachers, or guests) to view teachers list
                        .requestMatchers(HttpMethod.GET, "/api/teachers", "/api/teachers/**").permitAll()

                        // ✅ Student endpoints
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/student/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasAnyRole("STUDENT", "TEACHER")

                        // ✅ Teacher endpoints
                        .requestMatchers("/api/teachers/me", "/api/teachers/me/**").hasRole("TEACHER")
                        .requestMatchers("/api/teachers/profile", "/api/teachers/profile/**").hasRole("TEACHER")
                        // Backward compatibility (but only for teachers' private actions)
                        .requestMatchers(HttpMethod.PUT, "/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/teacher/**").hasRole("TEACHER")

                        // ✅ Admin endpoints (future)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ✅ Everything else must be authenticated
                        .anyRequest().authenticated()
                )
                // ✅ Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
