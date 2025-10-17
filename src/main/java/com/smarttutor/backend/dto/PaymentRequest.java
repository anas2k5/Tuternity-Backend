package com.smarttutor.backend.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;
    private String paymentMethod; // e.g. "UPI", "Credit Card", "Razorpay"
    private double amount;
}
