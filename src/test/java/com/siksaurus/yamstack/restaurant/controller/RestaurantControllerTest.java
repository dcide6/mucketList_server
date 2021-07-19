package com.siksaurus.yamstack.restaurant.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.restaurant.service.RestaurantVO;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void getRestaurant() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

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

        ReviewVO review = new ReviewVO();
        review.setId(123l);
        review.setGenTime(LocalDate.now());
        review.setVisitTime(LocalDate.now().minusDays(30));
        review.setImagePath("https://aws.s3.com/image1.jpg");
        review.setComment("가성비 훌륭");
        review.setShared(true);
        review.setCompany(Company.ALONE);
        review.setLikeCount(10l);
        review.setILiked(false);
        review.setNickName("얌얌박사");
        review.setRestaurantName("식당");

        RestaurantVO vo = new RestaurantVO();
        vo.setRestaurant(restaurant);
        vo.setYamPick(56);
        vo.setVisitNum(30);
        vo.setRecommend(25);
        vo.setReviewNum(1);
        vo.setCompany(Company.ALONE);
        vo.setTags(new HashSet<>(Arrays.asList("가성비", "혼밥", "재방문")));
        vo.setFoods(new HashSet<>(Arrays.asList("김치찌개", "계란말이", "라면사리")));
        vo.setReviews(Arrays.asList(review));

        given(this.restaurantService.getRestaurantVO(123l, null, null)).willReturn(vo);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/restaurant/123").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void getRestaurantByKaKao() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

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

        ReviewVO review = new ReviewVO();
        review.setId(123l);
        review.setGenTime(LocalDate.now());
        review.setVisitTime(LocalDate.now().minusDays(30));
        review.setImagePath("https://aws.s3.com/image1.jpg");
        review.setComment("가성비 훌륭");
        review.setShared(true);
        review.setCompany(Company.ALONE);
        review.setLikeCount(10l);
        review.setILiked(false);
        review.setNickName("얌얌박사");
        review.setRestaurantName("식당");

        RestaurantVO vo = new RestaurantVO();
        vo.setRestaurant(restaurant);
        vo.setYamPick(56);
        vo.setVisitNum(30);
        vo.setRecommend(25);
        vo.setReviewNum(1);
        vo.setCompany(Company.ALONE);
        vo.setTags(new HashSet<>(Arrays.asList("가성비", "혼밥", "재방문")));
        vo.setFoods(new HashSet<>(Arrays.asList("김치찌개", "계란말이", "라면사리")));
        vo.setReviews(Arrays.asList(review));

        given(this.restaurantService.getRestaurantByApiId("123456")).willReturn(restaurant);
        given(this.restaurantService.getRestaurantVO(123l, null, null)).willReturn(vo);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/restaurant/kakao/123456").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void getRestaurantList() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        List<RestaurantVO> voList = new ArrayList<>();
        for(long i=100; i<=190; i+=20) {
            Restaurant restaurant = Restaurant.builder()
                    .apiId("123456")
                    .name("식당"+i)
                    .addName("서울 동작구 상도동"+i+"-"+(i+55))
                    .roadAddName("서울 동작구 성대로"+i+"길 "+(i+55))
                    .region1depth("서울")
                    .region2depth("동작구")
                    .region3depth("상도동")
                    .category1depth("음식점")
                    .category2depth("한식")
                    .x("127."+i)
                    .y("37."+i)
                    .build();
            restaurant.setId(123);

            ReviewVO review = new ReviewVO();
            review.setId(i);
            review.setGenTime(LocalDate.now());
            review.setVisitTime(LocalDate.now().minusDays(i));
            review.setImagePath("https://aws.s3.com/image"+i+".jpg");
            review.setComment("가성비 훌륭");
            review.setShared(true);
            review.setCompany(Company.ALONE);
            review.setLikeCount(10l);
            review.setILiked(false);
            review.setNickName("얌얌박사");
            review.setRestaurantName("식당"+i);

            RestaurantVO vo = new RestaurantVO();
            vo.setRestaurant(restaurant);
            vo.setDist(i);
            vo.setYamPick(56);
            vo.setVisitNum(30);
            vo.setRecommend(25);
            vo.setReviewNum(1);
            vo.setCompany(Company.ALONE);
            vo.setTags(new HashSet<>(Arrays.asList("가성비", "혼밥", "재방문")));
            vo.setFoods(new HashSet<>(Arrays.asList("김치찌개", "계란말이", "라면사리")));
            vo.setReviews(Arrays.asList(review));

            voList.add(vo);
        }

        RestaurantDTO.selectRestaurantDTO dto = new RestaurantDTO.selectRestaurantDTO();
        dto.setMode("want");
        dto.setX("120.123456");
        dto.setY("36.123456");

        final PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "nullValue");
        Page<RestaurantVO> restaurantVOS = new PageImpl(voList, pageable, 10);

        given(this.restaurantService.getRestaurantVOList(any(), any(), any(), any())).willReturn(restaurantVOS);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/restaurant/list?page=0&size=5")
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
