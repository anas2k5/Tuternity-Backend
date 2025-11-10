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

    // Create a new booking (Student)
    @Transactional
    public Booking createBooking(String studentEmail, Long teacherId, Long availabilityId) {
        logger.info("Creating booking for student: {}, teacherId: {}, availabilityId: {}", studentEmail, teacherId, availabilityId);

        User studentUser = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + studentEmail));

        Student student = studentRepository.findByUserId(studentUser.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));

        Availability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + availabilityId));

        if (slot.isBooked() || bookingRepository.existsByAvailabilityId(availabilityId)) {
            throw new RuntimeException("This slot has already been booked. Please choose another one.");
        }

        slot.setBooked(true);
        availabilityRepository.save(slot);

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setTeacher(teacher);
        booking.setAvailability(slot);
        booking.setDate(slot.getDate());
        booking.setStartTime(slot.getStartTime());
        booking.setEndTime(slot.getEndTime());
        booking.setStatus("PENDING");

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking successfully created with ID: {}", savedBooking.getId());
        return savedBooking;
    }

    // Get all bookings by Student (using studentId)
    public List<BookingResponseDTO> getBookingsByStudent(Long studentId) {
        logger.debug("Fetching bookings for studentId: {}", studentId);
        return bookingRepository.findByStudent_Id(studentId).stream().map(this::mapToStudentDTO).toList();
    }

    // Get all bookings by Student (using userId)
    public List<BookingResponseDTO> getBookingsByStudentUserId(Long userId) {
        logger.debug("Fetching bookings for student userId: {}", userId);
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found for userId: " + userId));
        return bookingRepository.findByStudent_Id(student.getId()).stream().map(this::mapToStudentDTO).toList();
    }

    // Get all bookings by Teacher (using teacherId)
    public List<BookingResponseDTO> getBookingsByTeacher(Long teacherId) {
        logger.debug("Fetching bookings for teacherId: {}", teacherId);
        return bookingRepository.findByTeacher_Id(teacherId).stream().map(this::mapToTeacherDTO).toList();
    }

    // Get all bookings by Teacher (using userId)
    public List<BookingResponseDTO> getBookingsByTeacherUserId(Long userId) {
        logger.debug("Fetching bookings for teacher userId: {}", userId);

        TeacherProfile teacher = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for userId: " + userId));

        return bookingRepository.findByTeacher_Id(teacher.getId()).stream().map(this::mapToTeacherDTO).toList();
    }

    // Helper – map booking for Student view
    private BookingResponseDTO mapToStudentDTO(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(b.getId());
        dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
        dto.setTimeSlot((b.getStartTime() != null ? b.getStartTime().toString() : "") + " - " + (b.getEndTime() != null ? b.getEndTime().toString() : ""));
        dto.setStatus(b.getStatus());
        dto.setMeetingLink(b.getMeetingLink()); // NEW

        if (b.getTeacher() != null) {
            // Force-load user safely (avoid NPE)
            if (b.getTeacher().getUser() != null) {
                dto.setTeacherName(b.getTeacher().getUser().getName());
            }
            dto.setSubject(b.getTeacher().getSubject());
            dto.setSkills(b.getTeacher().getSkills());
        }
        return dto;
    }

    // Helper – map booking for Teacher view
    private BookingResponseDTO mapToTeacherDTO(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(b.getId());
        dto.setDate(b.getDate() != null ? b.getDate().toString() : "-");
        dto.setTimeSlot((b.getStartTime() != null ? b.getStartTime().toString() : "") + " - " + (b.getEndTime() != null ? b.getEndTime().toString() : ""));
        dto.setStatus(b.getStatus());
        dto.setMeetingLink(b.getMeetingLink()); // NEW

        if (b.getStudent() != null && b.getStudent().getUser() != null) {
            dto.setStudentName(b.getStudent().getUser().getName());
            dto.setStudentEmail(b.getStudent().getUser().getEmail());
        }
        return dto;
    }

    // Cancel booking (Student)
    @Transactional
    public void cancelBooking(Long bookingId, String email) {
        logger.info("Student {} attempting to cancel booking ID {}", email, bookingId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if (!booking.getStudent().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel a paid booking");
        }

        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        logger.info("Booking ID {} cancelled successfully by student {}", bookingId, email);
    }

    // Cancel booking (Teacher)
    @Transactional
    public void cancelBookingByTeacher(Long bookingId, String teacherEmail) {
        logger.info("Teacher {} attempting to cancel booking ID {}", teacherEmail, bookingId);

        User teacherUser = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher user not found"));

        TeacherProfile teacher = teacherProfileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel a paid booking");
        }

        Availability slot = booking.getAvailability();
        slot.setBooked(false);
        availabilityRepository.save(slot);

        booking.setStatus("CANCELLED_BY_TEACHER");
        bookingRepository.save(booking);
    }

    // Confirm booking (Teacher)
    @Transactional
    public void confirmBooking(Long bookingId, String teacherEmail) {
        logger.info("Teacher {} confirming booking {}", teacherEmail, bookingId);

        User teacherUser = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TeacherProfile teacher = teacherProfileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized to confirm this booking");
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only pending bookings can be confirmed");
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
        logger.info("Booking ID {} confirmed successfully by teacher {}", bookingId, teacherEmail);
    }

    // Mark as completed (Teacher)
    @Transactional
    public void markAsCompleted(Long bookingId, String teacherEmail) {
        logger.info("Teacher {} marking booking {} as completed", teacherEmail, bookingId);

        User teacherUser = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        TeacherProfile teacher = teacherProfileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        if (!"PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only paid bookings can be marked as completed");
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);

        logger.info("Booking ID {} marked as COMPLETED by teacher {}", bookingId, teacherEmail);
    }

    // NEW: update meeting link (teacher sets/edits meeting URL)
    @Transactional
    public Booking updateMeetingLink(Long bookingId, String teacherEmail, String meetingLink) {
        logger.info("Teacher {} updating meeting link for booking {}", teacherEmail, bookingId);

        User teacherUser = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher user not found"));

        TeacherProfile teacher = teacherProfileRepository.findByUser(teacherUser)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized to update meeting link for this booking");
        }

        booking.setMeetingLink(meetingLink);
        Booking saved = bookingRepository.save(booking);
        logger.info("Meeting link updated for booking {} by teacher {}", bookingId, teacherEmail);
        return saved;
    }
}
