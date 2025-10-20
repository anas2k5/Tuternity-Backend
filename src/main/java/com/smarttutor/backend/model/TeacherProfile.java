package com.smarttutor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // CRITICAL IMPORT
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "teacher_profiles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // CRITICAL FIX
public class TeacherProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;
    private String subject;
    private Double hourlyRate;

    @Column(nullable = true)
    private String skills;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}