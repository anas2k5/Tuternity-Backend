package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.service.BookingService;
import com.smarttutor.backend.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    public BookingController(BookingService bookingService, JwtUtil jwtUtil) {
        this.bookingService = bookingService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Booking createBooking(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @RequestBody BookingRequest request) {

        logger.info("Received booking request for teacher ID: {}", request.getTeacherId());

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization token is missing or invalid.");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        logger.info("Extracted email: {}", email);

        return bookingService.createBooking(email, request);
    }
}
