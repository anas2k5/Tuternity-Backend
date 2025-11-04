package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.LoginRequest;
import com.smarttutor.backend.dto.RegisterRequest;
import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// 🟢 Allow frontend access (update to your frontend port — 5173 for Vite)
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ✅ Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        System.out.println(">>> register endpoint hit, email: " + req.getEmail());
        try {
            User user = authService.register(
                    req.getName(),
                    req.getEmail(),
                    req.getPassword(),
                    Role.valueOf(req.getRole().toUpperCase())
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "role", user.getRole().name()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ✅ Login endpoint (returns token + full user info)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            User user = authService.findUserByEmail(req.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "id", user.getId(),
                    "role", user.getRole().name(),
                    "name", user.getName(),
                    "email", user.getEmail()
            ));

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
