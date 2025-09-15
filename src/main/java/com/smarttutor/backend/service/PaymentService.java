package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.PaymentRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;

    public PaymentService(PaymentRepository paymentRepo, BookingRepository bookingRepo) {
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
    }

    public Payment makePayment(PaymentRequest req) {
        Booking booking = bookingRepo.findById(req.getBookingId()).orElseThrow();

        // Simulated gateway
        String txnId = UUID.randomUUID().toString();
        String status = "SUCCESS";

        // Save payment
        Payment payment = Payment.builder()
                .amount(req.getAmount())
                .status(status)
                .transactionId(txnId)
                .booking(booking)
                .build();

        Payment savedPayment = paymentRepo.save(payment);

        // Update booking status if payment success
        if ("SUCCESS".equals(status)) {
            booking.setStatus("PAID");
            bookingRepo.save(booking);
        }

        return savedPayment;
    }

    public Payment getPaymentByBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow();
        return paymentRepo.findByBooking(booking).orElseThrow();
    }
}
