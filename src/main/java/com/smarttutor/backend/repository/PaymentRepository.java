package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
}
