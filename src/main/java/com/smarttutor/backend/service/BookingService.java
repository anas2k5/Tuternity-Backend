package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.*;
import com.smarttutor.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final TeacherProfileRepository teacherRepo;
    private final AvailabilityRepository availabilityRepo;
    private final NotificationService notificationService; // ✅ Added NotificationService

    public BookingService(BookingRepository bookingRepo,
                          UserRepository userRepo,
                          TeacherProfileRepository teacherRepo,
                          AvailabilityRepository availabilityRepo,
                          NotificationService notificationService) { // ✅ Added to constructor
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
        this.availabilityRepo = availabilityRepo;
        this.notificationService = notificationService;
    }

    public Booking createBooking(BookingRequest request) {
        User student = userRepo.findById(request.getStudentId()).orElseThrow();
        TeacherProfile teacher = teacherRepo.findById(request.getTeacherId()).orElseThrow();
        Availability availability = availabilityRepo.findById(request.getAvailabilityId()).orElseThrow();

        if (availability.isBooked()) {
            throw new RuntimeException("Slot already booked!");
        }

        // Mark the slot as booked
        availability.setBooked(true);
        availabilityRepo.save(availability);

        // Create the booking
        Booking booking = Booking.builder()
                .student(student)
                .teacher(teacher)
                .availability(availability)
                .bookingTime(LocalDateTime.now())
                .status("CONFIRMED")
                .build();

        Booking savedBooking = bookingRepo.save(booking);

        // ✅ Send confirmation email to student
        notificationService.sendEmail(
                student.getEmail(),
                "Booking Confirmed",
                "Hello " + student.getName() + ",\n\nYour session with " +
                        teacher.getUser().getName() + " is confirmed for " +
                        availability.getStartTime().toLocalDate() + " at " + availability.getStartTime().toLocalTime() +
                        ".\n\nThank you for booking with us!"
        );

        // ✅ Optional: Send notification email to teacher
        notificationService.sendEmail(
                teacher.getUser().getEmail(),
                "New Booking Received",
                "Hello " + teacher.getUser().getName() + ",\n\nYou have a new booking from " +
                        student.getName() + " scheduled for " +
                        availability.getStartTime().toLocalDate() + " at " + availability.getStartTime().toLocalTime() + "."
        );

        return savedBooking;
    }

    public List<Booking> getStudentBookings(Long studentId) {
        User student = userRepo.findById(studentId).orElseThrow();
        return bookingRepo.findByStudent(student);
    }

    public List<Booking> getTeacherBookings(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId).orElseThrow();
        return bookingRepo.findByTeacher(teacher);
    }
}
