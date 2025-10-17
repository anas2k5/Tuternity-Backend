package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.Student;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.StudentRepository;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, StudentRepository studentRepo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register new user
    public User register(String name, String email, String password, Role role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role(role)
                .build();

        User savedUser = userRepo.save(user);

        // ✅ Create student entry automatically for student users
        if (role.name().equalsIgnoreCase("STUDENT")) {
            Student student = new Student();
            student.setUser(savedUser);
            studentRepo.save(student);
        }

        return savedUser;
    }

    // ✅ Login and generate JWT
    public String login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // ✅ Get user details by email (used in controller)
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
