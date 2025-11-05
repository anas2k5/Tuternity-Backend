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

    // ✅ Create a new booking (Student side)
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

        // Mark slot as booked
        slot.setBooked(true);
        availabilityRepository.save(slot);

        // Create booking
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

    // ✅ Get all bookings by Student ID (Student Dashboard)
    public List<BookingResponseDTO> getBookingsByStudent(Long studentId) {
        return bookingRepository.findByStudent_Id(studentId).stream()
                .map(b -> {
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

    // ✅ Get all bookings by Teacher ID (Teacher Dashboard)
    public List<BookingResponseDTO> getBookingsByTeacher(Long teacherId) {
        return bookingRepository.findByTeacher_Id(teacherId).stream()
                .map(b -> {
                    BookingResponseDTO dto = new BookingResponseDTO();
                    dto.setId(b.getId());
                    dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
                    dto.setTimeSlot(b.getStartTime() + " - " + b.getEndTime());
                    dto.setStatus(b.getStatus());

                    if (b.getStudent() != null) {
                        dto.setStudentName(b.getStudent().getName());
                        dto.setStudentEmail(b.getStudent().getEmail());
                    }
                    return dto;
                }).toList();
    }

    // ✅ Cancel a booking (Student)
    @Transactional
    public void cancelBooking(Long bookingId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStudent().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        // Mark slot as available again
        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        // Update booking status
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    // ✅ Cancel a booking (Teacher)
    @Transactional
    public void cancelBookingByTeacher(Long bookingId, String teacherEmail) {
        User teacherUser = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher user not found"));

        TeacherProfile teacher = teacherProfileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        // Free the slot again
        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        booking.setStatus("CANCELLED_BY_TEACHER");
        bookingRepository.save(booking);
    }
}
