package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StripeController {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    // ✅ Create Stripe Checkout Session
    @PostMapping("/create-checkout-session/{bookingId}")
    public ResponseEntity<?> createCheckoutSession(@PathVariable Long bookingId) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID " + bookingId));

            // ❌ Prevent double payment
            if (paymentRepository.existsByBookingId(bookingId)) {
                Payment existing = paymentRepository.findByBookingId(bookingId).get();
                if ("SUCCESS".equalsIgnoreCase(existing.getStatus())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Payment already completed for this booking"));
                }
            }

            double amount = booking.getTeacher().getHourlyRate() * 100; // convert to paise/cents

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:3000/paymentSuccess?bookingId=" + bookingId)
                    .setCancelUrl("http://localhost:3000/paymentCancel?bookingId=" + bookingId)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount((long) amount)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Tutoring Session with " +
                                                                            booking.getTeacher().getUser().getName())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            Payment payment = paymentRepository.findByBookingId(bookingId)
                    .orElse(Payment.builder()
                            .booking(booking)
                            .amount(booking.getTeacher().getHourlyRate())
                            .status("PENDING")
                            .transactionId(session.getId())
                            .build());

            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", session.getId());
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Verify Payment Success
    @GetMapping("/success/{bookingId}")
    public ResponseEntity<?> verifyPayment(@PathVariable Long bookingId) {
        try {
            Payment payment = paymentRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for booking " + bookingId));

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID " + bookingId));

            if (!"SUCCESS".equalsIgnoreCase(payment.getStatus())) {
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);

                booking.setStatus("PAID");
                bookingRepository.save(booking);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment verified successfully");
            response.put("bookingId", bookingId);
            response.put("paymentStatus", payment.getStatus());
            response.put("bookingStatus", booking.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Cancel Payment
    @GetMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelPayment(@PathVariable Long bookingId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByBookingId(bookingId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
            }

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment cancelled successfully",
                    "bookingId", bookingId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Get all payments for a specific teacher (Dashboard stats)
    @GetMapping("/payments/teacher/{teacherId}")
    public ResponseEntity<?> getPaymentsByTeacher(@PathVariable Long teacherId) {
        try {
            List<Payment> payments = paymentRepository.findAll().stream()
                    .filter(p -> p.getBooking() != null
                            && p.getBooking().getTeacher() != null
                            && teacherId.equals(p.getBooking().getTeacher().getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Get all payments for a specific student (for future use)
    @GetMapping("/payments/student/{studentId}")
    public ResponseEntity<?> getPaymentsByStudent(@PathVariable Long studentId) {
        try {
            List<Payment> payments = paymentRepository.findAll().stream()
                    .filter(p -> p.getBooking() != null
                            && p.getBooking().getStudent() != null
                            && studentId.equals(p.getBooking().getStudent().getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
