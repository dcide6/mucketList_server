package com.siksaurus.yamstack.review.service;


import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.siksaurus.yamstack.review.controller.ReviewDTO;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

//    /* 여기얌 - 리뷰 리스트 조회*/
////    public List<ReviewVO> getReviewList() {
////        return reviewRepository.findAll().stream().map(ReviewVO::new).collect(Collectors.toList());
////    }
/* 여기얌 - 리뷰 리스트 조회*/
    public Page<Review> getReviewList(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    /* 얌/여기얌 - 리뷰 상세 조회*/
    @Transactional
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).get();
    }

    /* 얌 - 리뷰 등록*/
    @Transactional
    public Long createReview(ReviewDTO.CreateReviewDTO dto) {
        return reviewRepository.save(dto.toEntity()).getId();
    }

}
