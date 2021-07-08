package com.siksaurus.yamstack.yam.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/yam")
@RequiredArgsConstructor
public class YamController {

    private final YamService yamService;
    private final AccountService accountService;
    private final RestaurantService restaurantService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @GetMapping("/metaInfo")
    public ResponseEntity<MetaInfo> getYamFilterInfo(@RequestHeader(value = "x-auth-token") String token) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        MetaInfo metaInfo = yamService.getYamListMetaInfo(email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(metaInfo);
    }

    @PostMapping
    public ResponseEntity<Page<Yam>> getYamsByEmail(@RequestHeader(value = "x-auth-token") String token,
                                                    @RequestBody YamDTO.filterYamInfo filter,
                                                    YamPageRequest pageable) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Page<Yam> yams = yamService.getYamListFilter(email, filter, pageable.of());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yams);
    }

    @PostMapping("/restaurant/{id}")
    public ResponseEntity<Yam> saveYamFromRestaurant(@RequestHeader(value = "x-auth-token") String token,
                                                     @PathVariable long id) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Account account = accountService.getAccountByEmail(email);
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        Yam yam = yamService.saveYamFromRestaurant(account, restaurant);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yam);
    }

    @PutMapping
    public ResponseEntity<Yam> updateYam(@RequestBody YamDTO.updateYam dto) {

        Yam yam = yamService.updateYamFromRequest(dto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yam);
    }

    @PutMapping("/visit")
    public ResponseEntity<Yam> updateYamVisit(@RequestBody YamDTO.updateYamVisit dto) {

        Yam yam = yamService.updateYamVisitFromRequest(dto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteYam(@PathVariable long id, @RequestParam boolean isClosed) {

        Yam yam = yamService.getYamById(id);
        if (isClosed) {
            Restaurant restaurant = yam.getRestaurant();
            restaurant.addClosedCount();
            restaurantService.saveRestaurant(restaurant);
            yamService.deleteYam(yam);
        } else {
            yam.setCompeteTime(LocalDate.now());
            yam.setGood(false);
            yamService.saveYam(yam);
        }

        CommonResponse response = CommonResponse.builder()
                .code("YAM DELETE")
                .status(200)
                .message("YAM DELETE ID : "+ id)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
