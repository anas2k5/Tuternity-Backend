package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.service.TeacherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/profile/{userId}")
    public TeacherProfile createOrUpdateProfile(@PathVariable Long userId, @RequestBody TeacherProfileRequest request) {
        return teacherService.createOrUpdateProfile(userId, request);
    }

    @GetMapping("/{teacherId}")
    public TeacherProfile getProfile(@PathVariable Long teacherId) {
        return teacherService.getProfile(teacherId);
    }

    // ✅ New endpoint to get all teachers
    @GetMapping
    public List<TeacherProfile> getAllTeachers() {
        return teacherService.getAllTeachers();
    }
}
