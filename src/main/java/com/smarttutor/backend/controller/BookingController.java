package com.smarttutor.backend.controller;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.dto.BookingResponseDTO;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    private final BookingService bookingService;

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

    @GetMapping("/student/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByStudentUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByStudentUserId(userId));
    }

    @GetMapping("/teacher/{userId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByTeacherUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByTeacherUserId(userId));
    }

    @DeleteMapping("/teacher/cancel/{bookingId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> cancelBookingByTeacher(
            @PathVariable Long bookingId,
            Authentication authentication) {
        bookingService.cancelBookingByTeacher(bookingId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> markBookingAsCompleted(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            bookingService.markAsCompleted(bookingId, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "Booking marked as completed successfully",
                    "bookingId", bookingId,
                    "status", "COMPLETED"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Confirm booking
    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            bookingService.confirmBooking(bookingId, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "Booking confirmed successfully",
                    "bookingId", bookingId,
                    "status", "CONFIRMED"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
