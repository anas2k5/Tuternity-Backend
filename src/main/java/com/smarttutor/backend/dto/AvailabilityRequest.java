package com.smarttutor.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AvailabilityRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
