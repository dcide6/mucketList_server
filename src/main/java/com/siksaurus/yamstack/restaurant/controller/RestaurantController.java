package com.siksaurus.yamstack.restaurant.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.restaurant.service.RestaurantVO;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final AccountService accountService;
    private final RestaurantService restaurantService;
    private final YamService yamService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestHeader(value = "x-auth-token") String token,
                                                       @RequestBody RestaurantDTO.createRestaurantDTO dto) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Account account = accountService.getAccountByEmail(email);
        Restaurant restaurant = restaurantService.getRestaurantFromRequest(dto);
        Yam yam = yamService.saveYamFromRequest(account, restaurant, dto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(restaurant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantVO> getRestaurantDetail(@PathVariable Long id) {
        RestaurantVO vo = restaurantService.getRestaurantVO(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vo);
    }
}
