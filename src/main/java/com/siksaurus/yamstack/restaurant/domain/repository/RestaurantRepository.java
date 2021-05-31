package com.siksaurus.yamstack.restaurant.domain.repository;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
