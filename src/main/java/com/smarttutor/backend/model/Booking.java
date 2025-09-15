package com.smarttutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @OneToOne
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

    private LocalDateTime bookingTime;
    private String status; // PENDING, CONFIRMED, PAID, CANCELLED
}
