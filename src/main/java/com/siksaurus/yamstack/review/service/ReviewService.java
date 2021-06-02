package com.siksaurus.yamstack.review.service;

import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review getReview(Review review) {
        return reviewRepository.findById(review.getId());
    }

    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }
}
