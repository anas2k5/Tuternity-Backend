package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByTeacherProfile(TeacherProfile teacherProfile);
}
