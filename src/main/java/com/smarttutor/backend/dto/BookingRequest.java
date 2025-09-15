package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private Long studentId;
    private Long teacherId;
    private Long availabilityId;
}
