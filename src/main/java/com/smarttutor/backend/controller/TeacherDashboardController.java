package com.smarttutor.backend.controller;

import com.smarttutor.backend.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher-dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeacherDashboardController {

    private final TeacherDashboardService teacherDashboardService;

    // ✅ Fetch teacher dashboard stats (used in frontend dashboard)
    @GetMapping("/{userId}/stats")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getTeacherStats(@PathVariable Long userId) {
        return ResponseEntity.ok(teacherDashboardService.getTeacherStats(userId));
    }
}
