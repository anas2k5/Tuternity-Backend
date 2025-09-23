package com.smarttutor.backend.service;

import com.smarttutor.backend.model.*;
import com.smarttutor.backend.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final TeacherProfileRepository teacherRepo;

    public ReviewService(ReviewRepository reviewRepo, UserRepository userRepo, TeacherProfileRepository teacherRepo) {
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
    }

    public Review leaveReview(Long studentId, Long teacherId, int rating, String comment) {
        User student = userRepo.findById(studentId).orElseThrow();
        TeacherProfile teacher = teacherRepo.findById(teacherId).orElseThrow();

        Review review = Review.builder()
                .student(student)
                .teacher(teacher)
                .rating(rating)
                .comment(comment)
                .build();

        return reviewRepo.save(review);
    }

    public List<Review> getTeacherReviews(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId).orElseThrow();
        return reviewRepo.findByTeacher(teacher);
    }
}
