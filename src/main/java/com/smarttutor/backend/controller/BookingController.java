package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.service.BookingService;
import com.smarttutor.backend.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    public BookingController(BookingService bookingService, JwtUtil jwtUtil) {
        this.bookingService = bookingService;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint to CREATE a new booking (POST)
    @PostMapping
    public Booking createBooking(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @RequestBody BookingRequest request) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization token is missing or invalid.");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        return bookingService.createBooking(email, request);
    }

    // Endpoint to RETRIEVE a student's bookings (GET)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Booking>> getStudentBookings(@PathVariable Long studentId) {
        List<Booking> bookings = bookingService.getStudentBookings(studentId);
        return ResponseEntity.ok(bookings);
    }

    // ✅ NEW: Endpoint to CANCEL a booking (DELETE)
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authorizationHeader.substring(7);
        String studentEmail = jwtUtil.extractUsername(token);

        bookingService.cancelBooking(bookingId, studentEmail);

        return ResponseEntity.noContent().build();
    }
}