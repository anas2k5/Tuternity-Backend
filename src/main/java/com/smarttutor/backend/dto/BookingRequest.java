package com.smarttutor.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long teacherId;

    // CRITICAL FIX: The ID of the specific slot being booked
    private Long availabilityId;

    // Note: The date and timeSlot fields below are now redundant
    // because the information is contained in the availabilityId,
    // but they are kept for compatibility with your existing code structure.
    private LocalDate date;
    private String timeSlot;
}