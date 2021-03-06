package com.siksaurus.yamstack.review.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.review.domain.ReviewLike;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.repository.LikeRepository;
import com.siksaurus.yamstack.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final AccountService accountService;

    public Long updateLike(String user_mail, Long review_id) {
        Optional<Review> review = reviewRepository.findById(review_id);
        if (!review.isPresent()) return -1l;
        Account account = accountService.getAccountByEmail(user_mail);
        Optional<ReviewLike> reviewLike= checkAlreadyLike(account, review.get());
        if (!reviewLike.isPresent()) {
            //ReviewLike newReviewLike =
            likeRepository.save(ReviewLike.builder().review(review.get()).account(account).build());
            return 1l;
        }else{
            likeRepository.delete(reviewLike.get());
            return 2l;
        }
    }

    private Optional<ReviewLike> checkAlreadyLike(Account account, Review review) {
        return likeRepository.findByAccountAndReview(account, review);
    }
}
