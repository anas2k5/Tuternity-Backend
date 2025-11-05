package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherProfileRequest {
    private String name;
    private String subject;
    private String skills;
    private Integer experienceYears;
    private Double hourlyRate;
    private String bio;
    private String city;
    private Boolean available; // Wrapper instead of primitive
}
