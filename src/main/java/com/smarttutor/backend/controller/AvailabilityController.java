package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // ✅ Add a new availability slot for the authenticated teacher
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Availability> addAvailability(
            @RequestBody Availability request,
            Authentication authentication) {

        String teacherEmail = authentication.getName();
        Availability savedSlot = availabilityService.addAvailability(teacherEmail, request);
        return ResponseEntity.ok(savedSlot);
    }

    // ✅ Fetch logged-in teacher’s availability slots
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Availability>> getMyAvailabilities(Authentication authentication) {
        String teacherEmail = authentication.getName();
        List<Availability> availabilities = availabilityService.getAvailabilitiesByTeacherEmail(teacherEmail);
        return ResponseEntity.ok(availabilities);
    }

    // ✅ Public: fetch availability slots by teacher ID
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Availability>> getByTeacher(@PathVariable Long teacherId) {
        List<Availability> availabilities = availabilityService.getAvailabilitiesByTeacherId(teacherId);
        return ResponseEntity.ok(availabilities);
    }

    // ✅ Delete a slot owned by the teacher
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            Authentication authentication) {

        String teacherEmail = authentication.getName();
        availabilityService.deleteAvailability(teacherEmail, id);
        return ResponseEntity.noContent().build();
    }
}
