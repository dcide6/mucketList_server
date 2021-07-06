package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.yam.domain.Yam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ReviewControllerTest extends ControllerTest {

    @MockBean
    ReviewService reviewService;

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

        ReviewVO reviewVO = new ReviewVO(review, yam.getAccount().getName(),"맛집",1l, true);
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
        ReviewDTO.CreateReviewDTO dto = new ReviewDTO.CreateReviewDTO();
        dto.setComment("맛집임 추천");
        dto.setCompany(Company.FRIEND);
        dto.setImagePath("");
        dto.setShared(true);

        MockMultipartFile reviewdata = new MockMultipartFile("reviewdata", "",
                "application/json", objectMapper.writeValueAsString(dto).getBytes(Charset.forName("UTF-8")));

        MockMultipartFile image = new MockMultipartFile("image","test.jpg",
                "multipart/form-data","<<jpg data>>".getBytes());


        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        given(reviewService.createReview(dto, image)).willReturn(8l);


        //when
        ResultActions result = mockMvc.perform(
                multipart("/api/v1/review")
                        .file(reviewdata)
                        .file(image)
                        .part(new MockPart("image", image.getBytes()))
                        .part(new MockPart("reviewdata",objectMapper.writeValueAsString(dto).getBytes()))
                        .param("reviewdata", objectMapper.writeValueAsString(dto))
                        .contentType(objectMapper.writeValueAsString(dto))
                        .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void delete() {
    }
}