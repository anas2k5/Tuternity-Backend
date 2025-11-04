package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherProfileRequest {
    private String name;
    private String subject;
    private String skills;
    private int experienceYears;
    private double hourlyRate;
    private String bio;
    private String city;
    private boolean available;
}
