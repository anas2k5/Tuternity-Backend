package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.PaymentRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;

    public PaymentService(PaymentRepository paymentRepo, BookingRepository bookingRepo) {
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
    }

    public Payment makePayment(PaymentRequest request) {
        // 1️⃣ Fetch booking
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Booking already paid");
        }

        // 2️⃣ Create payment
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(request.getAmount())
                .status("SUCCESS") // Mock payment
                .transactionId("TXN" + System.currentTimeMillis())
                .build();

        Payment savedPayment = paymentRepo.save(payment);

        // 3️⃣ Update booking status
        booking.setStatus("PAID");
        bookingRepo.save(booking);

        return savedPayment;
    }

    public Payment getPaymentByBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ✅ Use Optional to avoid compilation errors
        Optional<Payment> paymentOpt = paymentRepo.findByBooking(booking);

        return paymentOpt.orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
