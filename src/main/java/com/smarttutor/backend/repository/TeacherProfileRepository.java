package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User; // CRITICAL: Import User entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    /**
     * Finds a TeacherProfile entity by the associated User entity.
     * This is required for secure 'Manage Profile' functionality (using the JWT-extracted User).
     */
    Optional<TeacherProfile> findByUser(User user);
}