package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ✅ Find booking by slot (Availability)
    Optional<Booking> findByAvailability(Availability availability);

    // ✅ Check if a slot (availabilityId) is already booked
    boolean existsByAvailabilityId(Long availabilityId);

    // ✅ Get all bookings for a specific student
    List<Booking> findByStudent_Id(Long studentId);

    // ✅ Get all bookings for a specific teacher
    List<Booking> findByTeacher_Id(Long teacherId);

    // ✅ Check if a student has already booked a teacher on a given date
    boolean existsByStudentAndTeacherAndDate(Student student, TeacherProfile teacher, LocalDate date);
}
