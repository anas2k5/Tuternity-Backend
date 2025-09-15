package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.service.BookingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/student/{studentId}")
    public List<Booking> getStudentBookings(@PathVariable Long studentId) {
        return bookingService.getStudentBookings(studentId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<Booking> getTeacherBookings(@PathVariable Long teacherId) {
        return bookingService.getTeacherBookings(teacherId);
    }
}
