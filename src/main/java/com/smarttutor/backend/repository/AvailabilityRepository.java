package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByTeacherAndBookedFalse(TeacherProfile teacher);

    Optional<Availability> findByTeacherAndStartTimeAndEndTime(
            TeacherProfile teacher,
            LocalTime startTime,
            LocalTime endTime
    );

    // ✅ Add this to fix your AvailabilityService error
    List<Availability> findByTeacher_User_Id(Long userId);
}
