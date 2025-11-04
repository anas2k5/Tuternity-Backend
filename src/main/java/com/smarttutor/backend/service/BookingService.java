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

    // ✅ Create a new booking
    @Transactional
    public Booking createBooking(String studentEmail, Long teacherId, Long availabilityId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        studentRepository.findByUserId(student.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Availability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) {
            throw new RuntimeException("This slot is already booked");
        }

        // ✅ Mark slot as booked
        slot.setBooked(true);
        availabilityRepository.save(slot);

        // ✅ Create booking entry
        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setTeacher(teacher);
        booking.setAvailability(slot);
        booking.setDate(slot.getDate());
        booking.setStartTime(slot.getStartTime());
        booking.setEndTime(slot.getEndTime());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // ✅ Get all bookings by student ID
    public List<BookingResponseDTO> getBookingsByStudent(Long studentId) {
        List<Booking> bookings = bookingRepository.findByStudent_Id(studentId);

        return bookings.stream().map(b -> {
            BookingResponseDTO dto = new BookingResponseDTO();
            dto.setId(b.getId());
            dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
            dto.setTimeSlot(b.getStartTime() + " - " + b.getEndTime());
            dto.setStatus(b.getStatus());

            if (b.getTeacher() != null) {
                dto.setTeacherName(b.getTeacher().getUser().getName());
                dto.setSubject(b.getTeacher().getSubject());
                dto.setSkills(b.getTeacher().getSkills());
            }

            return dto;
        }).toList();
    }

    // ✅ Get all bookings by teacher ID
    public List<BookingResponseDTO> getBookingsByTeacher(Long teacherId) {
        List<Booking> bookings = bookingRepository.findByTeacher_Id(teacherId);

        return bookings.stream().map(b -> {
            BookingResponseDTO dto = new BookingResponseDTO();
            dto.setId(b.getId());
            dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
            dto.setTimeSlot(b.getStartTime() + " - " + b.getEndTime());
            dto.setStatus(b.getStatus());

            if (b.getStudent() != null) {
                dto.setTeacherName(b.getStudent().getName());
            }

            return dto;
        }).toList();
    }

    // ✅ Cancel a booking
    @Transactional
    public void cancelBooking(Long bookingId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStudent().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
