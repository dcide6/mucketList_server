package com.siksaurus.yamstack.yam.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.MealTime;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.ReviewLike;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
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

public class YamControllerTest extends ControllerTest {

    @MockBean
    YamService yamService;

    @MockBean
    RestaurantService restaurantService;

    @Test
    public void getMetaInfo() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        MetaInfo metaInfo = new MetaInfo();
        Map<String, Set<String>> regionInfo = new HashMap<>();
        regionInfo.put("??????", new HashSet<>(Arrays.asList("?????????","?????????","??????","?????????")));
        regionInfo.put("?????????", new HashSet<>(Arrays.asList("????????????","?????????","?????????")));

        metaInfo.setRegionInfo(regionInfo);
        metaInfo.setCategories(new HashSet<>(Arrays.asList("??????", "??????", "??????")));
        metaInfo.setTags(new HashSet<>(Arrays.asList("??????","?????????","?????????")));
        metaInfo.setYamSize(20);
        metaInfo.setCompleteSize(10);
        metaInfo.setNoRevisitSize(5);

        given(yamService.getYamListMetaInfo("test@aaa.bbb")).willReturn(metaInfo);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/yam/metaInfo").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getYamListTest() throws Exception {
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

        Account another = Account.builder()
                .email("test@ccc.ddd")
                .password("1234")
                .name("another")
                .role(AccountRole.USER)
                .build();

        List<Yam> yams = new ArrayList<>();
        for(long i=100; i<=190; i+=20) {
            Restaurant restaurant = Restaurant.builder()
                    .apiId(String.valueOf(i*10))
                    .name("??????"+i)
                    .addName("?????? ????????? ?????????"+i+"-"+(i+55))
                    .roadAddName("?????? ????????? ?????????"+i+"??? "+(i+55))
                    .region1depth("??????")
                    .region2depth("?????????")
                    .region3depth("?????????")
                    .category1depth("?????????")
                    .category2depth("??????")
                    .x("127."+i)
                    .y("37."+i)
                    .build();
            restaurant.setId(i);

            Yam yam = new Yam();
            yam.setId(i+1);
            yam.setGenTime(LocalDate.now().minusDays(i/10));
            yam.setGood(true);
            yam.setAccount(account);
            yam.setRestaurant(restaurant);
            yam.setFoods(new HashSet<>(Arrays.asList(Food.builder().name("????????????").build(), Food.builder().name("????????????").build())));
            yam.setTags(new HashSet<>(Arrays.asList(Tag.builder().name("??????").build(), Tag.builder().name("?????????").build())));
            yam.setMemo("?????? ?????? ??????");

            Review review = new Review();
            review.setId(i+2);
            review.setYam(yam);
            review.setGenTime(LocalDate.now().minusDays(i/20));
            review.setVisitTime(LocalDate.now().minusDays(i/20 - 5));
            review.setImagePath("https://s3.aws.com/image"+i+".jpg");
            review.setComment("????????? ??????"+i);
            review.setShared(true);
            review.setCompany(Company.ALONE);
            review.setMealTime(MealTime.DINNER);

            ReviewLike like = ReviewLike.builder().account(another).review(review).build();
            like.setId(i+3);
            review.setReviewLikes(new HashSet<>(Arrays.asList(like)));

            yam.setReview(review);
            yams.add(yam);
        }

        YamDTO.filterYamInfo filter = new YamDTO.filterYamInfo();
        filter.setRegion("?????????");
        filter.setCategory("??????");
        filter.setSearchName("??????");
        filter.setTags(Arrays.asList("??????"));
        filter.setMode(2);

        final PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "genTime");
        Page<Yam> yamList = new PageImpl(yams, pageable, 10);

        given(this.yamService.getYamListFilter(any(), any(), any())).willReturn(yamList);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/yam?page=0&size=10&direction=DESC")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void saveYamFromRestaurantTest() throws Exception {

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
                .name("????????? ??????")
                .addName("?????? ????????? ????????? 123-123")
                .roadAddName("?????? ????????? ?????????123??? 123")
                .region1depth("??????")
                .region2depth("?????????")
                .region3depth("?????????")
                .category1depth("?????????")
                .category2depth("??????")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .build();
        restaurant.setId(456);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .build();
        yam.setId(123);

        given(this.restaurantService.getRestaurantById(456)).willReturn(restaurant);
        given(this.yamService.saveYamFromRestaurant(any(), any())).willReturn(yam);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/yam/restaurant/456").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateYamTest() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        YamDTO.updateYam dto = new YamDTO.updateYam();
        dto.setId(123);
        dto.setMemo("??? ??????");
        dto.setTags(new HashSet<>(Arrays.asList("????????????", "????????????")));
        dto.setFoods(new HashSet<>(Arrays.asList("????????????")));

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .apiId("123456")
                .name("????????? ??????")
                .addName("?????? ????????? ????????? 123-123")
                .roadAddName("?????? ????????? ?????????123??? 123")
                .region1depth("??????")
                .region2depth("?????????")
                .region3depth("?????????")
                .category1depth("?????????")
                .category2depth("??????")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .build();
        restaurant.setId(456);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .build();
        yam.setId(123);
        yam.setTags(new HashSet<>(Arrays.asList(Tag.builder().name("????????????").build(), Tag.builder().name("????????????").build())));
        yam.setFoods(new HashSet<>(Arrays.asList(Food.builder().name("????????????").build())));
        yam.setMemo("??? ??????");

        given(this.yamService.updateYamFromRequest(any())).willReturn(yam);

        //when
        ResultActions result = mockMvc.perform(put("/api/v1/yam")
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
    public void updateYamVisitTest() throws Exception {
        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        YamDTO.updateYamVisit dto = new YamDTO.updateYamVisit();
        dto.setId(123);
        dto.setReVisit(true);

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .apiId("123456")
                .name("????????? ??????")
                .addName("?????? ????????? ????????? 123-123")
                .roadAddName("?????? ????????? ?????????123??? 123")
                .region1depth("??????")
                .region2depth("?????????")
                .region3depth("?????????")
                .category1depth("?????????")
                .category2depth("??????")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .build();
        restaurant.setId(456);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .build();
        yam.setId(123);
        yam.setGood(true);

        given(this.yamService.updateYamVisitFromRequest(any())).willReturn(yam);

        //when
        ResultActions result = mockMvc.perform(put("/api/v1/yam/visit")
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
    public void deleteYamTest() throws Exception {

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
                .name("????????? ??????")
                .addName("?????? ????????? ????????? 123-123")
                .roadAddName("?????? ????????? ?????????123??? 123")
                .region1depth("??????")
                .region2depth("?????????")
                .region3depth("?????????")
                .category1depth("?????????")
                .category2depth("??????")
                .x("127.05902969025047")
                .y("37.51207412593136")
                .build();
        restaurant.setId(456);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .build();
        yam.setId(123);

        given(this.yamService.getYamById(any())).willReturn(yam);

        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/yam/123?isClosed=false").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
