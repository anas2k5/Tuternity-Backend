package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.service.TeacherService;
import com.smarttutor.backend.security.JwtUtil; // CRITICAL: Import JwtUtil
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final JwtUtil jwtUtil; // Inject JwtUtil

    // Updated constructor to inject JwtUtil
    public TeacherController(TeacherService teacherService, JwtUtil jwtUtil) {
        this.teacherService = teacherService;
        this.jwtUtil = jwtUtil;
    }

    // Helper method to extract email securely
    private String extractEmail(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization token is missing or invalid.");
        }
        String token = authorizationHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    // -------------------------------------------------------------
    // 1. GET: Fetch the profile of the currently logged-in teacher (for "Manage Profile" view)
    // Maps to: GET /api/teacher/profile
    @GetMapping("/profile")
    public TeacherProfile getMyProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String email = extractEmail(authorizationHeader);
        // Assuming TeacherService has a method to find profile by user email
        return teacherService.getProfileByEmail(email);
    }

    // 2. PUT: Update the profile of the currently logged-in teacher (for "Manage Profile" save)
    // Maps to: PUT /api/teacher/profile
    @PutMapping("/profile") // Use PUT or PATCH for updates
    public TeacherProfile updateMyProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody TeacherProfileRequest request) {

        String email = extractEmail(authorizationHeader);
        // Assuming TeacherService has a method to update profile by user email
        return teacherService.updateProfile(email, request);
    }
    // -------------------------------------------------------------

    // 3. GET: Get a specific teacher profile (for student viewing)
    @GetMapping("/{teacherId}")
    public TeacherProfile getProfile(@PathVariable Long teacherId) {
        return teacherService.getProfile(teacherId);
    }

    // 4. GET: New endpoint to get all teachers
    @GetMapping
    public List<TeacherProfile> getAllTeachers() {
        return teacherService.getAllTeachers();
    }
}