package com.smarttutor.backend.controller;

import com.smarttutor.backend.model.Review;
import com.smarttutor.backend.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review leaveReview(@RequestParam Long studentId,
                              @RequestParam Long teacherId,
                              @RequestParam int rating,
                              @RequestParam String comment) {
        return reviewService.leaveReview(studentId, teacherId, rating, comment);
    }

    @GetMapping("/{teacherId}")
    public List<Review> getTeacherReviews(@PathVariable Long teacherId) {
        return reviewService.getTeacherReviews(teacherId);
    }
}
