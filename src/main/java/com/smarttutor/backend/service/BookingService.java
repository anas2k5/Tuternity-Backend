package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.BookingResponseDTO;
import com.smarttutor.backend.model.*;
import com.smarttutor.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AvailabilityRepository availabilityRepository;

    // ----------------------------------------
    // CREATE BOOKING
    // ----------------------------------------
    @Transactional
    public Booking createBooking(String studentEmail, Long teacherId, Long availabilityId) {

        User studentUser = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + studentEmail));

        Student student = studentRepository.findByUserId(studentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found"));

        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        Availability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        // Prevent double booking
        if (slot.isBooked() || bookingRepository.existsByAvailabilityId(availabilityId)) {
            throw new IllegalStateException("This time slot has already been booked.");
        }

        // Mark slot booked
        slot.setBooked(true);
        availabilityRepository.save(slot);

        Booking booking = Booking.builder()
                .student(student)
                .teacher(teacher)
                .availability(slot)
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status("PENDING")
                .build();

        return bookingRepository.save(booking);
    }

    // ----------------------------------------
    // GET BOOKINGS FOR STUDENT (userId)
    // ----------------------------------------
    public List<BookingResponseDTO> getBookingsByStudentUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found"));
        return bookingRepository.findByStudent_Id(student.getId()).stream().map(this::mapToStudentDTO).toList();
    }

    // ----------------------------------------
    // GET BOOKINGS FOR TEACHER (userId)
    // ----------------------------------------
    public List<BookingResponseDTO> getBookingsByTeacherUserId(Long userId) {
        TeacherProfile teacher = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found"));
        return bookingRepository.findByTeacher_Id(teacher.getId()).stream().map(this::mapToTeacherDTO).toList();
    }

    // ----------------------------------------
    // CANCEL BY STUDENT
    // ----------------------------------------
    @Transactional
    public void cancelBooking(Long bookingId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getStudent().getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized cancel attempt.");
        }

        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Paid booking cannot be canceled.");
        }

        booking.getAvailability().setBooked(false);
        availabilityRepository.save(booking.getAvailability());

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    // ----------------------------------------
    // CANCEL BY TEACHER
    // ----------------------------------------
    @Transactional
    public void cancelBookingByTeacher(Long bookingId, String teacherEmail) {

        TeacherProfile teacher = teacherProfileRepository.findByUser(
                userRepository.findByEmail(teacherEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("Unauthorized.");
        }

        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Paid booking cannot be canceled.");
        }

        booking.getAvailability().setBooked(false);
        availabilityRepository.save(booking.getAvailability());

        booking.setStatus("CANCELLED_BY_TEACHER");
        bookingRepository.save(booking);
    }

    // ----------------------------------------
    // CONFIRM
    // ----------------------------------------
    @Transactional
    public void confirmBooking(Long bookingId, String teacherEmail) {

        TeacherProfile teacher = teacherProfileRepository.findByUser(
                userRepository.findByEmail(teacherEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("Unauthorized.");
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Only pending bookings can be confirmed.");
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    // ----------------------------------------
    // COMPLETE
    // ----------------------------------------
    @Transactional
    public void markAsCompleted(Long bookingId, String teacherEmail) {

        TeacherProfile teacher = teacherProfileRepository.findByUser(
                userRepository.findByEmail(teacherEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("Unauthorized");
        }

        if (!"PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Only paid bookings can be completed.");
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);
    }

    // ----------------------------------------
    // UPDATE MEETING LINK
    // ----------------------------------------
    @Transactional
    public Booking updateMeetingLink(Long bookingId, String teacherEmail, String meetingLink) {

        TeacherProfile teacher = teacherProfileRepository.findByUser(
                userRepository.findByEmail(teacherEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("Unauthorized.");
        }

        booking.setMeetingLink(meetingLink);
        return bookingRepository.save(booking);
    }

    // -----------------------------
    // MAPPERS
    // -----------------------------
    private BookingResponseDTO mapToStudentDTO(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(b.getId());
        dto.setDate(b.getDate().toString());
        dto.setTimeSlot(b.getStartTime() + " - " + b.getEndTime());
        dto.setStatus(b.getStatus());
        dto.setMeetingLink(b.getMeetingLink());
        dto.setTeacherName(b.getTeacher().getUser().getName());
        dto.setSubject(b.getTeacher().getSubject());
        dto.setSkills(b.getTeacher().getSkills());
        return dto;
    }

    private BookingResponseDTO mapToTeacherDTO(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(b.getId());
        dto.setDate(b.getDate().toString());
        dto.setTimeSlot(b.getStartTime() + " - " + b.getEndTime());
        dto.setStatus(b.getStatus());
        dto.setMeetingLink(b.getMeetingLink());
        dto.setStudentName(b.getStudent().getUser().getName());
        dto.setStudentEmail(b.getStudent().getUser().getEmail());
        return dto;
    }
}
