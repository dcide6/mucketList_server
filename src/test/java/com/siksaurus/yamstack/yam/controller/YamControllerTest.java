package com.siksaurus.yamstack.yam.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
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
        regionInfo.put("서울", new HashSet<>(Arrays.asList("강남구","서초구","중구","동작구")));
        regionInfo.put("경기도", new HashSet<>(Arrays.asList("남양주시","구리시","성남시")));

        metaInfo.setRegionInfo(regionInfo);
        metaInfo.setCategories(new HashSet<>(Arrays.asList("한식", "중식", "일식")));
        metaInfo.setTags(new HashSet<>(Arrays.asList("혼밥","분위기","언젠간")));
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

        List<Yam> yams = new ArrayList<>();
        for(long i=100; i<=190; i+=10) {
            Restaurant restaurant = Restaurant.builder()
                    .apiId(String.valueOf(i*10))
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
            restaurant.setId(i);

            Yam yam = new Yam();
            yam.setId(i+1);
            yam.setGenTime(LocalDate.now().minusDays(i/10));
            yam.setGood(true);
            yam.setAccount(account);
            yam.setRestaurant(restaurant);
            yam.setFoods(new HashSet<>(Arrays.asList(Food.builder().name("제육볶음").build(), Food.builder().name("부대찌개").build())));
            yam.setTags(new HashSet<>(Arrays.asList(Tag.builder().name("혼밥").build(), Tag.builder().name("언젠간").build())));
            yam.setMemo("친구 추천 맛집");

            yams.add(yam);
        }

        YamDTO.filterYamInfo filter = new YamDTO.filterYamInfo();
        filter.setRegion("동작구");
        filter.setCategory("한식");
        filter.setSearchName("식당");
        filter.setTags(Arrays.asList("혼밥"));
        filter.setMode(1);

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
        ResultActions result = mockMvc.perform(post("/api/v1/yam/restaurant/456").headers(httpHeaders));

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
        dto.setMemo("얌 수정");
        dto.setTags(new HashSet<>(Arrays.asList("수정태그", "추가태그")));
        dto.setFoods(new HashSet<>(Arrays.asList("추가음식")));

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
        restaurant.setId(456);

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .build();
        yam.setId(123);
        yam.setTags(new HashSet<>(Arrays.asList(Tag.builder().name("수정태그").build(), Tag.builder().name("추가태그").build())));
        yam.setFoods(new HashSet<>(Arrays.asList(Food.builder().name("추가음식").build())));
        yam.setMemo("얌 수정");

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
