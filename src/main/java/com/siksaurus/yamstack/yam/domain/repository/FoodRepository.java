package com.siksaurus.yamstack.yam.domain.repository;

import com.siksaurus.yamstack.yam.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByName(String name);
}
