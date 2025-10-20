package com.smarttutor.backend.dto;

import lombok.Data;
// ... other imports

@Data // CRITICAL: This generates all getters (getName, getBio, getSkills, etc.)
public class TeacherProfileRequest {
    private String bio;
    private String subject;
    private String skills;
    private Double hourlyRate;
    private String name; // For updating user's name
}