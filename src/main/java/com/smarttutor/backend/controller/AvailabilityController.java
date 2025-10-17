package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.AvailabilityRequest;
import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.service.AvailabilityService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/{teacherId}")
    public Availability addAvailability(@PathVariable Long teacherId, @RequestBody AvailabilityRequest request) {
        return availabilityService.addAvailability(teacherId, request);
    }

    // FIXED endpoint
    @GetMapping("/teacher/{teacherId}")
    public List<Availability> getAvailability(@PathVariable Long teacherId) {
        return availabilityService.getTeacherAvailability(teacherId);
    }
}
