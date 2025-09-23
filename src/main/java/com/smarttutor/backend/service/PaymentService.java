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
    private final NotificationService notificationService; // ✅ Added NotificationService

    // ✅ Constructor updated to inject NotificationService
    public PaymentService(PaymentRepository paymentRepo,
                          BookingRepository bookingRepo,
                          NotificationService notificationService) {
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
        this.notificationService = notificationService;
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

            // ✅ Send payment success email to student
            notificationService.sendEmail(
                    booking.getStudent().getEmail(),
                    "Payment Successful",
                    "Hello " + booking.getStudent().getName() + ",\n\n" +
                            "Your payment of Rs." + req.getAmount() + " was successful for booking #" + booking.getId() + ".\n" +
                            "Transaction ID: " + txnId + "\n\nThank you for using SmartTutor!"
            );

            // ✅ Optional: Email notification to teacher
            notificationService.sendEmail(
                    booking.getTeacher().getUser().getEmail(),
                    "Payment Received for Booking",
                    "Hello " + booking.getTeacher().getUser().getName() + ",\n\n" +
                            "A payment of Rs." + req.getAmount() + " has been received for booking #" + booking.getId() + ".\n" +
                            "Transaction ID: " + txnId + "\n\nPlease prepare for the session."
            );
        }

        return savedPayment;
    }

    public Payment getPaymentByBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow();
        return paymentRepo.findByBooking(booking).orElseThrow();
    }
}
