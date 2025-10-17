package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Student;
import com.smarttutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUser(User user);
}
