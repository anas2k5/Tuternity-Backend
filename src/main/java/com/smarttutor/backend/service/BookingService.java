package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.BookingResponseDTO;
import com.smarttutor.backend.model.*;
import com.smarttutor.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public Booking createBooking(String studentEmail, Long teacherId, Long availabilityId) {
        User user = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Availability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) throw new RuntimeException("This slot is already booked");

        slot.setBooked(true);
        availabilityRepository.save(slot);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTeacher(teacher);
        booking.setAvailability(slot);
        booking.setStatus("CONFIRMED");
        booking.setDate(slot.getDate());
        booking.setTimeSlot(slot.getStartTime() + " - " + slot.getEndTime());

        return bookingRepository.save(booking);
    }

    public List<BookingResponseDTO> getBookingsByStudentId(Long studentId) {
        List<Booking> bookings = bookingRepository.findByUser_Id(studentId);

        return bookings.stream().map(b -> {
            BookingResponseDTO dto = new BookingResponseDTO();
            dto.setId(b.getId());
            dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
            dto.setTimeSlot(b.getTimeSlot() != null ? b.getTimeSlot() : "-");
            dto.setStatus(b.getStatus() != null ? b.getStatus() : "CONFIRMED");

            if (b.getTeacher() != null && b.getTeacher().getUser() != null) {
                dto.setTeacherName(b.getTeacher().getUser().getName());
                dto.setSubject(b.getTeacher().getSubject());
                dto.setSkills(b.getTeacher().getSkills());
            }

            return dto;
        }).toList();
    }

    @Transactional
    public void cancelBooking(Long bookingId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    // ✅ NEW: Fetch all bookings for a student (full entities)
    public List<Booking> getBookingsByStudent(Long studentId) {
        return bookingRepository.findByUser_Id(studentId);
    }

    // ✅ NEW: Fetch all bookings for a teacher (full entities)
    public List<Booking> getBookingsByTeacher(Long teacherId) {
        return bookingRepository.findByTeacher_Id(teacherId);
    }
}
