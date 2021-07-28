package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.MealTime;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.domain.ReviewLike;
import com.siksaurus.yamstack.review.service.LikeService;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.yam.controller.YamDTO;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.Yam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.ArgumentMatchers.any;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ReviewControllerTest extends ControllerTest {

    @MockBean
    ReviewService reviewService;

    @MockBean
    LikeService likeService;

    @Test
    void list() throws Exception {
        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = makeJwtAuthToken(role, expiredDate);
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

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
        List<ReviewVO> reviews = new ArrayList<>();
        for(long i=100; i<=190; i+=20) {
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

            Review review = new Review();
            review.setId(i+2);
            review.setYam(yam);
            review.setGenTime(LocalDate.now().minusDays(i/20));
            review.setVisitTime(LocalDate.now().minusDays(i/20 - 5));
            review.setImagePath("https://s3.aws.com/image"+i+".jpg");
            review.setComment("테스트 리뷰"+i);
            review.setShared(true);
            review.setCompany(Company.ALONE);
            review.setMealTime(MealTime.DINNNER);

            ReviewLike like = ReviewLike.builder().account(another).review(review).build();
            like.setId(i+3);
            review.setReviewLikes(new HashSet<>(Arrays.asList(like)));

            yam.setReview(review);
            yams.add(yam);

            ReviewVO reviewVO = new ReviewVO(review, yam.getAccount().getName(),"맛집",i,8l, true);
            reviews.add(reviewVO);
        }


        final PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "genTime");
        Page<ReviewVO> reviewList = new PageImpl(reviews, pageable, 10);

        given(this.reviewService.getReviewList(pageable, email)).willReturn(reviewList);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/review/list?page=0&size=10&direction=DESC")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void getReviewById() throws Exception {
        //given
        Long id = 1L;
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = makeJwtAuthToken(role, expiredDate);
        httpHeaders.add("x-auth-token",token);

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

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
                .account(account)
                .genTime(LocalDate.now())
                .restaurant(restaurant)
                .build();
        Review review = Review.builder()
                .id(id)
                .comment("맛집임 추천!")
                .company(Company.FRIEND)
                .imagePath("")
                .isShared(true)
                .visitTime(LocalDate.now())
                .yam(yam)
                .build();

        ReviewVO reviewVO = new ReviewVO(review, yam.getAccount().getName(),"맛집",1l,6l, true);
        given(reviewService.getReviewById(id, email)).willReturn(reviewVO);


        //when
        ResultActions result = mockMvc.perform(get("/api/v1/review/"+id).headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createReview() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));
        String token = httpHeaders.getFirst("x-auth-token");
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");


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

        ReviewDTO.CreateReviewDTO dto = ReviewDTO.CreateReviewDTO.builder()
                .yam(yam)
                .visitTime(LocalDate.now())
                .comment("맛집임 추천")
                .company(Company.FRIEND)
                .isShared(true)
                .build();


        MockMultipartFile reviewdata = new MockMultipartFile("reviewdata", "",
                "application/json", objectMapper.writeValueAsString(dto).getBytes(Charset.forName("UTF-8")));

        MockMultipartFile image = new MockMultipartFile("image","image.jpg",
                "multipart/form-data","<<jpg data>>".getBytes());



        given(reviewService.createReview(dto, image, email)).willReturn(0l);


        //when
        ResultActions result = mockMvc.perform(
                multipart("/api/v1/review")
                .file(image)
                .file(reviewdata).contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .accept(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void deleteReview() throws Exception {
        //given
        AccountRole role = AccountRole.USER;
        Long id = 1L;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = makeJwtAuthToken(role, expiredDate);
        httpHeaders.add("x-auth-token",token);

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

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
                .account(account)
                .genTime(LocalDate.now())
                .restaurant(restaurant)
                .build();
        Review review = Review.builder()
                .id(id)
                .comment("맛집임 추천!")
                .company(Company.FRIEND)
                .imagePath("")
                .isShared(true)
                .visitTime(LocalDate.now())
                .yam(yam)
                .build();

        given(reviewService.deleteReview(id, "test@aaa.bbb")).willReturn("Review [ " + id + " ] has been deleted.");

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/review/delete")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"review_id\": \"1\" }"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void updateReviewLike() throws Exception {

        //given
        AccountRole role = AccountRole.USER;
        Long id = 1l;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = makeJwtAuthToken(role, expiredDate);
        httpHeaders.add("x-auth-token",token);

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");


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
                .account(account)
                .genTime(LocalDate.now())
                .restaurant(restaurant)
                .build();
        Review review = Review.builder()
                .id(id)
                .comment("맛집임 추천!")
                .company(Company.FRIEND)
                .imagePath("")
                .isShared(true)
                .visitTime(LocalDate.now())
                .yam(yam)
                .build();

        given(likeService.updateLike("test@aaa.bbb", id)).willReturn(id);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/review/like")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"review_id\": \"1\" }"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk());

    }
}