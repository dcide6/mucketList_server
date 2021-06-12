package com.siksaurus.yamstack.review.service;

import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    /* 여기얌 - 리뷰 상세 조회*/
    @Transactional
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).get();
    }
}
