package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.Student;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.StudentRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.security.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final TeacherProfileRepository teacherRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo,
                       StudentRepository studentRepo,
                       TeacherProfileRepository teacherRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register new user
    public User register(String name, String email, String password, Role role) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role(role)
                .build();

        try {
            User savedUser = userRepo.save(user);

            // ✅ Automatically create Student or Teacher profiles
            switch (role) {
                case STUDENT -> {
                    Student student = new Student();
                    student.setUser(savedUser);
                    studentRepo.save(student);
                }
                case TEACHER -> {
                    TeacherProfile teacher = new TeacherProfile();
                    teacher.setUser(savedUser);
                    teacherRepo.save(teacher);
                }
            }

            return savedUser;

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email already exists. Please use another email.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Registration failed. Please try again.");
        }
    }

    // ✅ Login and generate JWT token
    public String login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // ✅ Fetch user details by email (for login response)
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
