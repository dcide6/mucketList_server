package com.siksaurus.yamstack.restaurant.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTest extends ControllerTest {

    @MockBean
    RestaurantService restaurantService;

    @MockBean
    YamService yamService;

    @Test
    public void createRestaurant() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .apiId("123456")
                .name("얌스택 식당")
                .addName("서울 동작구 상도동 123-123")
                .roadAddName("서울 동작구 성대로123길 123")
                .region1depth("서울")
                .region2depth("동작구")
                .region3depth("상도동")
                .category1depth("음식점")
                .category2depth("한식")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .build();
        restaurant.setId(123);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .memo("test")
                .build();

        RestaurantDTO.createRestaurantDTO dto = RestaurantDTO.createRestaurantDTO.builder()
                .id("123456")
                .place_name("얌스택 식당")
                .address_name("서울 동작구 상도동 123-123")
                .road_address_name("서울 동작구 성대로123길 123")
                .category_name("음식점 > 한식 > 분식")
                .place_url("http://place.map.kakao.com/26338954")
                .phone("02-123-1234")
                .category_group_code("")
                .category_group_name("")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .tags(new HashSet<>(Arrays.asList("분식", "미슐랭")))
                .foods(new HashSet<>(Arrays.asList("제육볶음", "된장찌개")))
                .memo("테스트 식당")
                .build();

        given(accountService.getAccountByEmail("test@aaa.bbb")).willReturn(account);
        given(restaurantService.getRestaurantFromRequest(dto)).willReturn(restaurant);
        given(yamService.saveYamFromRequest(account, restaurant, dto)).willReturn(yam);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/restaurant")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    }

}
