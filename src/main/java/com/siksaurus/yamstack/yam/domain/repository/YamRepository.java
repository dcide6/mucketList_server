package com.siksaurus.yamstack.yam.domain.repository;

import com.siksaurus.yamstack.yam.domain.Yam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface YamRepository extends JpaRepository<Yam, Long> {
    @EntityGraph(attributePaths = {"account", "restaurant", "foods", "tags", "review"})
    List<Yam> findByAccount_Email(String email);
    @EntityGraph(attributePaths = {"account", "restaurant", "foods", "tags", "review"})
    Page<Yam> findByAccount_Email(String email, Pageable pageable);
    @EntityGraph(attributePaths = {"account", "restaurant", "foods", "tags", "review"})
    List<Yam> findByRestaurant_Id(Long id);
}
