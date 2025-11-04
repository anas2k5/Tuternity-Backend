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

    // ✅ Teacher adds a new availability slot
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Availability> addAvailability(@RequestBody Availability request, Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(availabilityService.addAvailability(email, request));
    }

    // ✅ Teacher gets their own slots
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Availability>> getMyAvailabilities(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(availabilityService.getAvailabilitiesByTeacherEmail(email));
    }

    // ✅ Public: get all slots of a teacher by teacherId
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Availability>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesByTeacherId(teacherId));
    }

    // ✅ Teacher deletes a slot
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        availabilityService.deleteAvailability(email, id);
        return ResponseEntity.noContent().build();
    }
}
