package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ DTO for booking requests
 * Sent from frontend as JSON body:
 * {
 *   "teacherId": 3,
 *   "availabilityId": 7
 * }
 */
@Getter
@Setter
public class BookingRequest {
    private Long teacherId;
    private Long availabilityId;
}
