package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * CRITICAL: Finds a User entity by their unique email address.
     * This is required by TeacherService for secure profile lookups.
     */
    Optional<User> findByEmail(String email);
}