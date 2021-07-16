package com.siksaurus.yamstack.review.domain.repository;

import com.siksaurus.yamstack.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"yam", "reviewLikes"})
    List<Review> findAllByGenTimeBetween(LocalDate from, LocalDate to);

    Optional<Review> findById(Long id);

    Page<Review> findAll(Pageable pageable);
}
