package com.siksaurus.yamstack.review.domain.repository;

import com.siksaurus.yamstack.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findById(long id);
}
