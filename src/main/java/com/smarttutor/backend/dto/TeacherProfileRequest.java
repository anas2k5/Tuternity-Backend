package com.smarttutor.backend.dto;

import lombok.Data;

@Data
public class TeacherProfileRequest {
    private String bio;
    private String subject;     // ✅ match TeacherProfile
    private Double hourlyRate;  // ✅ match TeacherProfile
}
