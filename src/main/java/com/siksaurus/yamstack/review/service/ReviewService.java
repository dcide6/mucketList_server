package com.siksaurus.yamstack.review.service;


import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.repository.AccountRepository;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.review.controller.ReviewDTO;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import com.siksaurus.yamstack.review.s3upload.S3Uploader;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final YamService yamService;
    private final S3Uploader s3Uploader;

    /* 여기얌 - 리뷰 리스트 조회*/
    public Page<Review> getReviewList(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    /* 얌/여기얌 - 리뷰 상세 조회*/
    public ReviewVO getReviewById(Long id) {
        Review review = reviewRepository.findById(id).get();
        ReviewVO reviewVO = new ReviewVO(review);
//        Yam yam = yamService.getYamById(review.getYam().getId());
//        Account account = accountRepository.findById(yam.getAccount().getId()).get();
//        reviewVO.setYamId(yam);
//        reviewVO.setAccount(account);
        return reviewVO;
    }

    /* 얌 - 리뷰 등록*/
    @Transactional
    public Long createReview(ReviewDTO.CreateReviewDTO dto, MultipartFile multipartFile) throws IOException {
        String filePath = s3Uploader.upload(multipartFile, "user-upload");
        dto.setImagePath(filePath);
        Yam yam = yamService.getYamById(dto.getYam().getId());
        dto.setYam(yam);
        return reviewRepository.save(dto.toEntity()).getId();
    }
//    /* 여기얌 - 리뷰 like*/
//    public Review updateReviewLike(Long id, Map<String, Integer> param) {
//        int likeStatus = param.getOrDefault("likeStatus", 1);
//        if (likeStatus == 0) likeStatus = 1;
//        Review review = reviewRepository.findById(id).get();
//        int likeNum = review.getLikeNum() + likeStatus/Math.abs(likeStatus);
//        review.setLikeNum(likeNum>0? likeNum:0);
//        return reviewRepository.save(review);
//    }
}
