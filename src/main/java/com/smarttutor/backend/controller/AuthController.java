package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.LoginRequest;
import com.smarttutor.backend.dto.RegisterRequest;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.service.AuthService;
import com.smarttutor.backend.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register API
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req) {
        System.out.println(">>> register endpoint hit, email: " + req.getEmail());
        return authService.register(
                req.getName(),
                req.getEmail(),
                req.getPassword(),
                Role.valueOf(req.getRole().toUpperCase()) // Convert String to Role enum
        );
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            String token = authService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(token);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(404).body("{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("{\"error\": \"Invalid credentials\"}");
        } catch (Exception ex) {
            ex.printStackTrace();  // ✅ Now you’ll see the actual error in console
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }

}
