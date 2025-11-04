package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "http://localhost:3000")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    /**
     * ✅ Add new availability for a teacher
     * Example request:
     * POST /api/availability?teacherId=3&date=2025-11-04&startTime=10:00&endTime=11:00
     */
    @PostMapping
    public ResponseEntity<?> addAvailability(
            @RequestParam Long teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        try {
            Availability slot = availabilityService.addAvailability(teacherId, date, startTime, endTime);
            return ResponseEntity.ok(slot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create slot: " + e.getMessage());
        }
    }

    /**
     * ✅ Get all availability slots for a specific teacher
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Availability>> getAvailabilityByTeacher(@PathVariable Long teacherId) {
        List<Availability> slots = availabilityService.getAvailabilityByTeacher(teacherId);
        return ResponseEntity.ok(slots);
    }

    /**
     * ✅ Delete a specific slot (teacher can remove it)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Long id) {
        try {
            availabilityService.deleteAvailability(id);
            return ResponseEntity.ok("Availability slot deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete slot: " + e.getMessage());
        }
    }

    /**
     * ✅ (Optional) Get all slots (admin or testing)
     */
    @GetMapping
    public ResponseEntity<List<Availability>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }
}
