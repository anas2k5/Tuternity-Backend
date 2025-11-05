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
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // ✅ Public - get all teachers
    @GetMapping
    public List<TeacherProfile> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // ✅ Public - get specific teacher by ID
    @GetMapping("/{id}")
    public TeacherProfile getTeacherById(@PathVariable Long id) {
        return teacherService.getProfile(id);
    }

    // ✅ Authenticated Teacher - get own profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile getOwnProfile(Authentication authentication) {
        String email = authentication.getName();
        return teacherService.getProfileByEmail(email);
    }

    // ✅ Authenticated Teacher - update own profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile updateOwnProfile(Authentication authentication,
                                           @RequestBody TeacherProfileRequest request) {
        String email = authentication.getName();
        return teacherService.updateProfile(email, request);
    }

    // ✅ Compatibility routes (for older frontend)
    @GetMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile getProfileCompat(Authentication authentication) {
        String email = authentication.getName();
        return teacherService.getProfileByEmail(email);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherProfile updateProfileCompat(Authentication authentication,
                                              @RequestBody TeacherProfileRequest request) {
        String email = authentication.getName();
        return teacherService.updateProfile(email, request);
    }
}
