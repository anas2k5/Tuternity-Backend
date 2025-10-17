package com.smarttutor.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long teacherId;
    private LocalDate date;
    private String timeSlot;
}
