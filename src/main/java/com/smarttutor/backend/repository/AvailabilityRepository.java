package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    // ✅ Get all available (non-booked) slots for a teacher
    List<Availability> findByTeacherAndBookedFalse(TeacherProfile teacher);

    // ✅ Find specific slot by time range (optional use)
    Optional<Availability> findByTeacherAndStartTimeAndEndTime(
            TeacherProfile teacher,
            LocalTime startTime,
            LocalTime endTime
    );

    // ✅ Find all availabilities by teacher’s userId (helper for profile view)
    List<Availability> findByTeacher_User_Id(Long userId);

    // ✅ Prevent duplicate slot creation for the same date & time
    boolean existsByTeacherAndDateAndStartTimeAndEndTime(
            TeacherProfile teacher,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}
