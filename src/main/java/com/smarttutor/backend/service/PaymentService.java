package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.PaymentRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;

    // ✅ Create or update payment before checkout
    public Payment makePayment(PaymentRequest request) {
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // prevent double payment
        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Booking already paid");
        }

        // check if a payment already exists
        Payment payment = paymentRepo.findByBooking(booking).orElse(null);

        if (payment == null) {
            payment = Payment.builder()
                    .booking(booking)
                    .amount(request.getAmount())
                    .status("PENDING")
                    .transactionId("TXN" + System.currentTimeMillis())
                    .build();
        } else {
            payment.setAmount(request.getAmount());
            payment.setStatus("PENDING");
            payment.setTransactionId("TXN" + System.currentTimeMillis());
        }

        return paymentRepo.save(payment);
    }

    // ✅ Update payment status to SUCCESS when Stripe confirms
    public Payment markPaymentSuccessful(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = paymentRepo.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("SUCCESS");
        booking.setStatus("PAID");

        paymentRepo.save(payment);
        bookingRepo.save(booking);

        return payment;
    }

    public Payment getPaymentByBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return paymentRepo.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
