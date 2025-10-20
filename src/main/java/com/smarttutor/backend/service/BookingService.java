package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;

    public BookingService(BookingRepository bookingRepository,
                          TeacherProfileRepository teacherProfileRepository,
                          UserRepository userRepository,
                          AvailabilityRepository availabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    // CREATE a new booking
    @Transactional
    public Booking createBooking(String userEmail, BookingRequest request) {
        // ... (Existing logic for fetching user, teacher, slot, marking booked, and saving)

        // 1. Fetch User (Student)
        User student = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Student not found: " + userEmail));

        // 2. Fetch Teacher
        TeacherProfile teacher = teacherProfileRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + request.getTeacherId()));

        // 3. Fetch Availability Slot
        Availability slot = availabilityRepository.findById(request.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + request.getAvailabilityId()));

        if (slot.isBooked()) {
            throw new RuntimeException("Slot is already booked!");
        }

        // 4. Mark Slot as Booked
        slot.setBooked(true);
        availabilityRepository.save(slot);

        // 5. Create Booking Record
        Booking booking = Booking.builder()
                .user(student)
                .teacher(teacher)
                .availability(slot)
                .status("CONFIRMED")
                .date(slot.getDate())
                .timeSlot(slot.getStartTime().toString() + " - " + slot.getEndTime().toString())
                .bookingTime(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    // ✅ NEW: Logic to cancel a booking
    @Transactional
    public void cancelBooking(Long bookingId, String studentEmail) {
        // 1. Find the Booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // 2. Authorization Check (Ensures student owns the booking)
        if (!booking.getUser().getEmail().equalsIgnoreCase(studentEmail)) {
            throw new RuntimeException("Authorization error: You can only cancel your own bookings.");
        }

        if (booking.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        // 3. Mark the Slot as Available
        Availability slot = booking.getAvailability();
        if (slot != null) {
            slot.setBooked(false);
            availabilityRepository.save(slot);
        }

        // 4. Update Booking Status
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    // RETRIEVE bookings for the frontend
    @Transactional(readOnly = true)
    public List<Booking> getStudentBookings(Long studentId) {
        // ... (Existing retrieval logic)
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + studentId));
        return bookingRepository.findByUser(student);
    }
}