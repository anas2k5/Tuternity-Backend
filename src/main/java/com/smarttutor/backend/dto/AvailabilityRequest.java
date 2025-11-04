package com.smarttutor.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AvailabilityRequest {
    private Long teacherId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
