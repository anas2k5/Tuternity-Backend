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
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        String studentEmail = authentication.getName();
        Booking booking = bookingService.createBooking(studentEmail, request.getTeacherId(), request.getAvailabilityId());
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

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.cancelBooking(bookingId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
    }

    @DeleteMapping("/teacher/cancel/{bookingId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> cancelBookingByTeacher(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.cancelBookingByTeacher(bookingId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully by teacher"));
    }

    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.confirmBooking(bookingId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Booking confirmed successfully", "bookingId", bookingId, "status", "CONFIRMED"));
    }

    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> markBookingAsCompleted(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.markAsCompleted(bookingId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Booking marked as completed successfully", "bookingId", bookingId, "status", "COMPLETED"));
    }

    // Update meeting link (Teacher)
    @PutMapping("/{bookingId}/meeting-link")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateMeetingLink(@PathVariable Long bookingId, @RequestBody Map<String, String> body, Authentication authentication) {
        String meetingLink = body.get("meetingLink");
        Booking updated = bookingService.updateMeetingLink(bookingId, authentication.getName(), meetingLink);
        return ResponseEntity.ok(Map.of("message", "Meeting link updated successfully", "bookingId", bookingId, "meetingLink", updated.getMeetingLink()));
    }
}
