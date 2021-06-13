package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* 얌/여기얌 - 리뷰 상세 조회*/
    @GetMapping("/{review_id}")
    public ResponseEntity<Review> getReviewById(@PathVariable("review_id")  Long review_id) {
        Review review = reviewService.getReviewById(review_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(review);
    }
    /* 얌 - 리뷰 등록*/
    @PostMapping("")
    public ResponseEntity<CommonResponse> createReview(@RequestBody ReviewDTO.CreateReviewDTO dto) {

        Long new_id = reviewService.createReview(dto);
        CommonResponse response =  CommonResponse.builder()
                .code("REVIEW_CREATED")
                .status(200)
                .message("review [" + new_id + "] has bean created")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}

