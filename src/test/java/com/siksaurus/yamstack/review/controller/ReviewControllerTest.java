package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.yam.domain.Yam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        Review review = Review.builder()
                .id(id)
                .comment("맛집임 추천!")
                .company(Company.FRIEND)
                .genTime(LocalDate.now())
                .imagePath("")
                .isShared(true)
                .likeNum(1231)
                .visitTime(LocalDate.now())
                .yam(new Yam())
                .build();

        given(reviewService.getReviewById(id)).willReturn(review);


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
        dto.setComment("맛집임 추천!");
        dto.setCompany(Company.FRIEND);
        dto.setGenTime(LocalDate.now());
        dto.setImagePath("");
        dto.setShared(true);

        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        given(reviewService.createReview(dto)).willReturn(8l);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/review/").headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(dto)));

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