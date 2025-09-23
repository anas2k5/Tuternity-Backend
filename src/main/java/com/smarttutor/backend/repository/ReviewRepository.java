package com.smarttutor.backend.repository;

import com.smarttutor.backend.model.Review;
import com.smarttutor.backend.model.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTeacher(TeacherProfile teacher);
}
