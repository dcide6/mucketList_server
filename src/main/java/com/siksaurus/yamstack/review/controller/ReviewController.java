package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.account.controller.AccountDTO;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import com.siksaurus.yamstack.review.s3upload.S3Uploader;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.LikeService;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.yam.controller.YamPageRequest;
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
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    /* 리뷰리스트 조회 */
    @GetMapping("/list")
    public ResponseEntity<Page<ReviewVO>> list(@RequestHeader(value = "x-auth-token") String token,
                                               ReviewPageRequest pageable) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Page<ReviewVO> reviews = reviewService.getReviewList(pageable.of(), email);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reviews);
    }

    /* 얌/여기얌 - 리뷰 상세 조회*/
    @GetMapping("/{review_id}")
    public ResponseEntity<ReviewVO> getReviewById(@RequestHeader(value = "x-auth-token") String token,
                                                  @PathVariable("review_id")  Long review_id) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");
        ReviewVO review = reviewService.getReviewById(review_id, email);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(review);
    }

    /* 리뷰 - 사진 등록*/
    @PostMapping("/image")
    public ResponseEntity<CommonResponse> uploadImage(@RequestHeader(value = "x-auth-token") String token,
                                                       @RequestPart MultipartFile multipartFile) throws IOException {
        String filePath = reviewService.saveImage(multipartFile);
        String code = "";
        String message = "";
        if (filePath == null || filePath.isEmpty()){
            code = "FAILED_TO_UPLOAD";
            message = "failed to upload file";
        }else{
            code = "SUCCESS_UPLOAD_IMAGE";
            message = "image uploaded as [" + filePath + "]";
        }
        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /* 리뷰 - 리뷰에 사진 등록*/
    @PostMapping("/image/{review_id}")
    public ResponseEntity<CommonResponse> saveImagePath(@RequestHeader(value = "x-auth-token") String token,
                                                      @PathVariable("review_id")  Long review_id,
                                                      @RequestPart MultipartFile multipartFile) throws IOException {
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");
        Long updatedReview = reviewService.saveImagePath(review_id, email, multipartFile);
        String code = "";
        String message = "";
        if (updatedReview < 0){
            code = "FAILED_TO_UPDATE_REVIEW";
            message = "check review status";
        }else{
            code = "SUCCESS_UPDATE_IMAGE";
            message = "image added at [" + updatedReview + "]";
        }
        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /* 얌 - 리뷰 등록*/
    @PostMapping("")
    public ResponseEntity<CommonResponse> createReview(@RequestHeader(value = "x-auth-token") String token,
                                                       @RequestBody ReviewDTO.CreateReviewDTO dto) throws IOException {
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Long new_id = reviewService.createReview(dto, email);

        String code = "";
        String message = "";
        if (new_id < 0){
            code = "FAILED_TO_CREATE";
            message = "failed to create review";
        }else{
            code = "REVIEW_UPDATED";
            message = "review [" + new_id + "] has bean created";
        }
        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /* 얌 - 리뷰 수정*/
    @PutMapping("/edit")
    public ResponseEntity<CommonResponse> updateReview(@RequestHeader(value = "x-auth-token") String token,
                                                       @RequestBody  ReviewDTO.UpdateReviewDTO dto) throws IOException {
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");
        Long updated_id = reviewService.updateReview(dto, email);
        String code = "";
        String message = "";
        if (updated_id < 0){
            code = "FAILED_TO_UPDATE";
            message = "failed to update review [" + updated_id + "]";
        }else{
            code = "REVIEW_UPDATED";
            message = "review [" + updated_id + "] has bean updated";
        }
        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);

    }

    /* 얌 - 리뷰 삭제*/
    @PostMapping("/delete")
    public ResponseEntity<CommonResponse> deleteReview(@RequestHeader(value = "x-auth-token") String token,
                               @RequestBody Map<String, Long> params) {
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        Long review_id = params.get("review_id");
        String email = (String) jwtAuthToken.getData().get("sub");
        String code = "";
        String message = reviewService.deleteReview(review_id, email);
        if (!message.startsWith("Review")){
            code = "DELETING_FAILED";
        }else{
            code = "REVIEW_DELETED";
        }

        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
    /* 얌 - 좋아요*/
    @PostMapping("/like")
    public ResponseEntity<CommonResponse> updateReviewLike(@RequestHeader(value = "x-auth-token") String token,
                                                   @RequestBody Map<String, String> params) {
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String user_mail = (String) jwtAuthToken.getData().get("sub");

        Long review_id = Long.parseLong(params.get("review_id"));
        Long result = -1L;
        if (user_mail != null){
            result = likeService.updateLike(user_mail, review_id);
        }

        String code = "";
        String message = "";
        if (result < 0){
            code = "LIKE_FAILED";
            message = "failed to update review's like";
        }else if(result == 1){
            code = "LIKED";
            message = "TRUE";
        }else if(result == 2){
            code = "DISLIKED";
            message = "FALSE";
        }

        CommonResponse response =  CommonResponse.builder()
                .code(code)
                .status(200)
                .message(message)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}

