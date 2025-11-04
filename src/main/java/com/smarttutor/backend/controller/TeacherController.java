package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "http://localhost:3000")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // ✅ Public - browse all teachers
    @GetMapping
    public List<TeacherProfile> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // ✅ Public - view specific teacher
    @GetMapping("/{id}")
    public TeacherProfile getTeacherById(@PathVariable Long id) {
        return teacherService.getProfile(id);
    }

    // ✅ Teacher-only routes (secured via Spring Security)
    @GetMapping("/profile")
    public TeacherProfile getTeacherProfile(@RequestParam String email) {
        return teacherService.getProfileByEmail(email);
    }

    @PutMapping("/profile")
    public TeacherProfile updateProfile(@RequestParam String email, @RequestBody TeacherProfileRequest request) {
        return teacherService.updateProfile(email, request);
    }
}
