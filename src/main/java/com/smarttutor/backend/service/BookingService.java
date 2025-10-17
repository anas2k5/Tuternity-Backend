package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.BookingRequest;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.BookingRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          TeacherProfileRepository teacherProfileRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.userRepository = userRepository;
    }

    public Booking createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        TeacherProfile teacher = teacherProfileRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + request.getTeacherId()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTeacher(teacher);
        booking.setStatus("CONFIRMED");
        booking.setDate(request.getDate());
        booking.setTimeSlot(request.getTimeSlot());

        return bookingRepository.save(booking);
    }
}
