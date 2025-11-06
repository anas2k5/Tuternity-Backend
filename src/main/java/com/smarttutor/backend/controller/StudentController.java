package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Student;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.StudentRepository;
import com.smarttutor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    // ✅ Get own student profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Student> getOwnProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        return ResponseEntity.ok(student);
    }

    // ✅ Update own student profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Student> updateOwnProfile(
            Authentication authentication,
            @RequestBody Student updatedProfile) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Update basic fields
        student.setPhone(updatedProfile.getPhone());
        student.setCity(updatedProfile.getCity());
        student.setEducationLevel(updatedProfile.getEducationLevel());
        student.setInterests(updatedProfile.getInterests());

        // Update user name if changed
        if (updatedProfile.getUser() != null &&
                updatedProfile.getUser().getName() != null &&
                !updatedProfile.getUser().getName().isBlank()) {
            user.setName(updatedProfile.getUser().getName());
            userRepository.save(user);
        }

        Student saved = studentRepository.save(student);
        return ResponseEntity.ok(saved);
    }
}
