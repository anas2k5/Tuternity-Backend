package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.dto.BookingRequest;

import com.smarttutor.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ✅ Create a booking (Student only)
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Booking> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        String studentEmail = authentication.getName();
        Booking booking = bookingService.createBooking(
                studentEmail,
                request.getTeacherId(),
                request.getAvailabilityId()
        );

        return ResponseEntity.ok(booking);
    }

    // ✅ Get bookings for a student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Booking>> getBookingsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(bookingService.getBookingsByStudent(studentId));
    }

    // ✅ Get bookings for a teacher
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Booking>> getBookingsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(bookingService.getBookingsByTeacher(teacherId));
    }

    // ✅ Cancel booking (Student only)
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        bookingService.cancelBooking(bookingId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
