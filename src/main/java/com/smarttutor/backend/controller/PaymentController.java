// PaymentController.java
package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.repository.PaymentRepository;
import com.smarttutor.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/student/{studentId}")
    public List<Object> getPaymentsByStudent(@PathVariable Long studentId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getBooking().getStudent().getId().equals(studentId))
                .map(p -> {
                    var b = p.getBooking();
                    return new Object() {
                        public final Long bookingId = b.getId();
                        public final String teacherName = b.getTeacher().getUser().getName();
                        public final String subject = b.getTeacher().getSubject();
                        public final String date = b.getDate() != null ? b.getDate().toString() : "-";
                        public final Double amount = p.getAmount();
                        public final String transactionId = p.getTransactionId();
                        public final String status = p.getStatus();
                    };
                })
                .collect(Collectors.toList());
    }
}
