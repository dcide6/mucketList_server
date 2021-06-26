package com.siksaurus.yamstack.restaurant.service;

import com.siksaurus.yamstack.restaurant.controller.RestaurantDTO;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.domain.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantByApiId(String apiId) {
        return restaurantRepository.findByApiId(apiId).get();
    }

    public List<Restaurant> getRestaurantByName(String name) {
        return restaurantRepository.findByName(name).get();
    }

    public Restaurant getRestaurantFromRequest(RestaurantDTO.createRestaurantDTO dto) {
        Restaurant rst = restaurantRepository.findByApiId(dto.getId()).orElse(null);
        if (rst == null) {
            if(dto.getId() != null) {
                String address = dto.getAddress_name();
                String category = dto.getCategory_name();
                String[] addresses = address.split(" ");
                String[] categories = category.split(">");

                Restaurant restaurant = Restaurant.builder()
                        .apiId(dto.getId())
                        .name(dto.getPlace_name())
                        .addName(dto.getAddress_name())
                        .roadAddName(dto.getRoad_address_name())
                        .region1depth(addresses[0])
                        .region2depth(addresses[1])
                        .region3depth(addresses[2])
                        .x(dto.getX())
                        .y(dto.getY())
                        .category1depth(categories[0].trim())
                        .category2depth(categories[1].trim())
                        .build();
                rst = this.saveRestaurant(restaurant);
            }else {
                Restaurant restaurant = Restaurant.builder()
                        .name(dto.getPlace_name())
                        .addName(dto.getAddress_name())
                        .category1depth(dto.getCategory_name())
                        .category2depth(dto.getCategory_name())
                        .build();
                rst = this.saveRestaurant(restaurant);
            }
        }

        return rst;
    }

    public void deleteRestaurant(Restaurant restaurant) {
        restaurantRepository.deleteById(restaurant.getId());
    }

}
