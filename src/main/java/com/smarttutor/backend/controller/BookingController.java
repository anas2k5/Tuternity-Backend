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

    // Endpoint to CREATE a new booking (Your existing POST method)
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

    // ✅ NEW: Endpoint to RETRIEVE a student's bookings
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Booking>> getStudentBookings(@PathVariable Long studentId) {
        List<Booking> bookings = bookingService.getStudentBookings(studentId);

        // Returns the list of bookings (or an empty list if none are found)
        return ResponseEntity.ok(bookings);
    }
}