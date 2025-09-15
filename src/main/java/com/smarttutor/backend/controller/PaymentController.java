package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.PaymentRequest;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment makePayment(@RequestBody PaymentRequest req) {
        return paymentService.makePayment(req);
    }

    @GetMapping("/{bookingId}")
    public Payment getPayment(@PathVariable Long bookingId) {
        return paymentService.getPaymentByBooking(bookingId);
    }
}
