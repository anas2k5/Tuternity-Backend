package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    Optional<Payment> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);
}
