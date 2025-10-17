package com.smarttutor.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {
    private LocalDateTime startTime;  // full datetime (date + time)
    private LocalDateTime endTime;    // full datetime (date + time)
}
