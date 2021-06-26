package com.siksaurus.yamstack.review.domain.repository;

import com.siksaurus.yamstack.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findById(Long id);

    Page<Review> findAll(Pageable pageable);
}
