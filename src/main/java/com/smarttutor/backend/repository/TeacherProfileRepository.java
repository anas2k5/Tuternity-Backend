package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
    Optional<TeacherProfile> findByUser(User user);
}
