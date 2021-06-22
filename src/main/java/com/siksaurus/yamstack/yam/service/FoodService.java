package com.siksaurus.yamstack.yam.service;

import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public Optional<Food> getFood(String name) {
        return this.foodRepository.findByName(name);
    }

    public Food saveFood(Food food) {
        return this.foodRepository.save(food);
    }

    public Set<Food> saveFoods(Set<String> foods) {
        if(foods == null) return new HashSet<>();
        Set<Food> foodsSet = new HashSet<>();
        foods.forEach(f -> {
            Food food = this.getFood(f).orElse(Food.builder().name(f).build());
            foodsSet.add(food);
        });
        try {
            List<Food> list = this.foodRepository.saveAll(foodsSet);
            Set<Food> set = new HashSet<>(list);
            return set;
        }catch (Exception e) {
            log.error("saveFoods error :"+e.getMessage());
            return new HashSet<>();
        }
    }
}
