package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ✅ Check if a slot is already booked
    Optional<Booking> findByAvailability(Availability availability);

    // ✅ Get all bookings of a specific student
    List<Booking> findByStudent_Id(Long studentId);

    // ✅ Get all bookings of a specific teacher
    List<Booking> findByTeacher_Id(Long teacherId);

    // ✅ Check if a student has already booked a teacher on a given date
    boolean existsByStudentAndTeacherAndDate(User student, TeacherProfile teacher, LocalDate date);
}
