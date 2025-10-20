package com.smarttutor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // CRITICAL IMPORT
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // CRITICAL FIX
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who booked (Student)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Teacher being booked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherProfile teacher;

    // Link to the specific Availability slot that was booked
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id")
    private Availability availability;

    private String status;

    private LocalDate date;
    private String timeSlot;
    private LocalDateTime bookingTime;
}