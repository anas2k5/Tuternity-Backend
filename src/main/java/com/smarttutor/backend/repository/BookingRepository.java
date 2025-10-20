package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all Booking entities where the 'user' field matches the provided User object.
     * CRITICAL: 'findByUser' must match the field name 'user' in the Booking.java entity.
     */
    List<Booking> findByUser(User user);
}