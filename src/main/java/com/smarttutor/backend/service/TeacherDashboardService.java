package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.Payment;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.PaymentRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final TeacherProfileRepository teacherProfileRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> getTeacherStats(Long userId) {

        // ✅ Get teacher profile
        TeacherProfile teacher = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user ID: " + userId));

        // ✅ Get teacher’s bookings
        List<Booking> bookings = bookingRepository.findByTeacher_Id(teacher.getId());

        // ✅ Count completed sessions
        long completedSessions = bookings.stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()))
                .count();

        // ✅ Count upcoming bookings (PAID or CONFIRMED)
        long upcomingBookings = bookings.stream()
                .filter(b -> "PAID".equalsIgnoreCase(b.getStatus()) || "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .count();

        // ✅ Calculate total earnings safely (convert Double → BigDecimal)
        Stream<BigDecimal> amountStream = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null &&
                        p.getBooking().getTeacher() != null &&
                        p.getBooking().getTeacher().getId().equals(teacher.getId()))
                .map(p -> {
                    Double amount = p.getAmount();
                    return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
                });

        // ✅ Explicit lambda types to prevent IDE confusion
        BigDecimal totalEarnings = amountStream
                .reduce(BigDecimal.ZERO, (BigDecimal a, BigDecimal b) -> a.add(b));

        // ✅ Chart data: sessions grouped by month
        Map<String, Long> sessionsByMonth = bookings.stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()) && b.getDate() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        Collectors.counting()
                ));

        // ✅ Build result map
        Map<String, Object> stats = new HashMap<>();
        stats.put("completedSessions", completedSessions);
        stats.put("upcomingBookings", upcomingBookings);
        stats.put("totalEarnings", totalEarnings);
        stats.put("sessionsByMonth", sessionsByMonth);

        return stats;
    }
}
