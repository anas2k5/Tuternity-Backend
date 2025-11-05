package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public Map<String, Object> createCheckoutSession(Long bookingId) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        double amount = booking.getTeacher().getHourlyRate();

        // Create or reuse existing payment record
        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        if (payment == null) {
            payment = new Payment();
            payment.setBooking(booking);
        }
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        // Stripe session creation
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
                                                .setUnitAmount((long) (amount * 100)) // paise
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Tutoring Session with " + booking.getTeacher().getUser().getName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        if (session == null || session.getUrl() == null) {
            throw new RuntimeException("Stripe session creation failed.");
        }

        // Store transaction ID
        payment.setTransactionId(session.getId());
        paymentRepository.save(payment);

        // Send URL back to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("url", session.getUrl());
        return response;
    }
}
