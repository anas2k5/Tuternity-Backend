package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponseDTO {
    private Long id;
    private String date;
    private String timeSlot;
    private String status;

    // Student view fields
    private String teacherName;
    private String subject;
    private String skills;

    // Teacher view fields
    private String studentName;
    private String studentEmail;

    // NEW: meeting link (visible to students and teachers)
    private String meetingLink;
}
