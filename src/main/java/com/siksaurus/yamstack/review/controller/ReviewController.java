package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.account.controller.AccountDTO;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.review.s3upload.S3Uploader;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.LikeService;
import com.siksaurus.yamstack.review.service.ReviewService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final LikeService likeService;


    @GetMapping("/list")
    public ResponseEntity<Page<Review>> list(final Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewList(pageable);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reviews);
    }

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
    public ResponseEntity<CommonResponse> createReview(@RequestPart("reviewdata") ReviewDTO.CreateReviewDTO dto,
                                                       @RequestPart("image") MultipartFile multipartFile//List<MultipartFile> multipartFiles
                                                                        ) throws IOException {
        Long new_id = reviewService.createReview(dto, multipartFile);

        CommonResponse response =  CommonResponse.builder()
                .code("REVIEW_CREATED")
                .status(200)
                .message("review [" + new_id + "] has bean created")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /* 얌 - 좋아요*/
    @PostMapping("/like")
    public ResponseEntity<String> updateReviewLike(@RequestBody Map<String, String> params) {
        String user_mail = params.get("user_mail");
        Long review_id = Long.parseLong(params.get("review_id"));
        Long result = -1L;
        if (user_mail != null){
            result = likeService.updateLike(user_mail, review_id);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("like [" + result.toString() + "] has bean changed");
    }
}

