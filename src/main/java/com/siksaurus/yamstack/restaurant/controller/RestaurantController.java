package com.siksaurus.yamstack.restaurant.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final AccountService accountService;
    private final RestaurantService restaurantService;
    private final YamService yamService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantDTO.createRestaurantDTO dto) {

        Account account = accountService.getAccountByEmail(dto.getEmail());
        Restaurant restaurant = restaurantService.getRestaurantFromRequest(dto);
        Yam yam = yamService.saveYamFromRequest(account, restaurant, dto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurant);
    }

}
