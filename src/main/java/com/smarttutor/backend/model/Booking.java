package com.smarttutor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Many bookings -> one student (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User student;

    // ✅ Many bookings -> one teacher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TeacherProfile teacher;

    // ✅ Availability linked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Availability availability;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private String status; // e.g. "BOOKED", "CANCELLED"
}
