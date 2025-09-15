package com.smarttutor.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String status;       // PENDING, SUCCESS, FAILED
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
