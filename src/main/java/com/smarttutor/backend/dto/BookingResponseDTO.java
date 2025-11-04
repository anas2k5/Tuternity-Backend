package com.smarttutor.backend.dto;

import lombok.Data;

@Data
public class BookingResponseDTO {
    private Long id;
    private String date;
    private String timeSlot;
    private String status;
    private String teacherName;
    private String subject;
    private String skills;
}
