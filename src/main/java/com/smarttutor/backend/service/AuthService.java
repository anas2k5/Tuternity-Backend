package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // Register user
    public User register(String name, String email, String password, Role role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role(role)
                .build();

        return userRepo.save(user);
    }

    // Login & generate JWT
    public String login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // ✅ Pass role name, not Role object
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());

    }
}
