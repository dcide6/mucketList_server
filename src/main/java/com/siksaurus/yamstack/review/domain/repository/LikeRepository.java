package com.siksaurus.yamstack.review.domain.repository;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.review.domain.ReviewLike;
import com.siksaurus.yamstack.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByAccountAndReview(Account account, Review review);
}
