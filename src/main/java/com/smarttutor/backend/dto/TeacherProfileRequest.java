package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherProfileRequest {
    private String bio;
    private String subject;
    private Double hourlyRate;
}
