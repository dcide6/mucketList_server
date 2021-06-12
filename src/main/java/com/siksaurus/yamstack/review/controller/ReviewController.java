package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    /* 여기얌 - 리뷰 상세 조회*/
    @GetMapping("/{review_id}")
    public ResponseEntity<Review> findReview(@PathVariable("review_id")  Long review_id) {
        Review review = reviewService.getReviewById(review_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(review);
    }
}

