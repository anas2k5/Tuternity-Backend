package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Booking;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudent(User student);
    List<Booking> findByTeacher(TeacherProfile teacher);
}
