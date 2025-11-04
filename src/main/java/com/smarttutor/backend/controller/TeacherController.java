package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // ✅ Public - view all teachers
    @GetMapping
    public List<TeacherProfile> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // ✅ Public - view specific teacher
    @GetMapping("/{id}")
    public TeacherProfile getTeacherById(@PathVariable Long id) {
        return teacherService.getProfile(id);
    }

    // ✅ Teacher-only - fetch own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile getTeacherProfile(Authentication authentication) {
        String email = authentication.getName(); // email comes from JWT
        return teacherService.getProfileByEmail(email);
    }

    // ✅ Teacher-only - update own profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile updateProfile(Authentication authentication, @RequestBody TeacherProfileRequest request) {
        String email = authentication.getName(); // extract email from JWT
        return teacherService.updateProfile(email, request);
    }
}
