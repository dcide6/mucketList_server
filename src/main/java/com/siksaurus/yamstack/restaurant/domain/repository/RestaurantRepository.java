package com.siksaurus.yamstack.restaurant.domain.repository;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByApiId(String apiId);

    Optional<Restaurant> findByName(String name);
}
