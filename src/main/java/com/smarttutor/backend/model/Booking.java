package com.smarttutor.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who booked
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Teacher being booked
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacher;

    private String status; // e.g., "CONFIRMED", "CANCELLED"
    private LocalDate date;
    private String timeSlot; // e.g., "10:00 AM - 11:00 AM"
}
