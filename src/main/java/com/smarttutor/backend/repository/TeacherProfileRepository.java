package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByUser(User user);

    Optional<TeacherProfile> findByUserId(Long userId);

    // ✅ Add this missing method to fix your DataSeeder compilation error
    Optional<TeacherProfile> findByUserEmail(String email);

    List<TeacherProfile> findByCityIgnoreCase(String city);
    List<TeacherProfile> findBySubjectIgnoreCase(String subject);
}
