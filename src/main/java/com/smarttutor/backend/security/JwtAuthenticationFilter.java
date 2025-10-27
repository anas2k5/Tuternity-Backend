package com.smarttutor.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // ✅ CRITICAL FIX: Tell Spring Security which paths to skip entirely
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip paths related to public authentication (login and register)
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // ✅ If the request was NOT skipped by shouldNotFilter, proceed with checking the token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // remove "Bearer "

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // If the token is expired, do not process further, but allow the chain to continue
            System.out.println("❌ JWT EXPIRED: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ If user not yet authenticated, validate the token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                String role = jwtUtil.extractRole(jwt);

                // Use the role exactly as stored in the token (e.g. ROLE_STUDENT)
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(authority)
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Optional: debug log (can remove later)
                System.out.println("? Extracted username: " + username);
                System.out.println("? Extracted role: " + role);
                System.out.println("? Authorities being set: " + List.of(authority));
            }
        }

        filterChain.doFilter(request, response);
    }
}