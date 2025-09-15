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

    public BookingService(BookingRepository bookingRepo, UserRepository userRepo,
                          TeacherProfileRepository teacherRepo, AvailabilityRepository availabilityRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
        this.availabilityRepo = availabilityRepo;
    }

    public Booking createBooking(BookingRequest request) {
        User student = userRepo.findById(request.getStudentId()).orElseThrow();
        TeacherProfile teacher = teacherRepo.findById(request.getTeacherId()).orElseThrow();
        Availability availability = availabilityRepo.findById(request.getAvailabilityId()).orElseThrow();

        if (availability.isBooked()) {
            throw new RuntimeException("Slot already booked!");
        }

        availability.setBooked(true);
        availabilityRepo.save(availability);

        Booking booking = Booking.builder()
                .student(student)
                .teacher(teacher)
                .availability(availability)
                .bookingTime(LocalDateTime.now())
                .status("CONFIRMED")
                .build();

        return bookingRepo.save(booking);
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
