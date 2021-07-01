package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReviewDTO {

        @NotNull
        private Long id;
        private Yam yam;
        private LocalDate genTime;
        private LocalDate visitTime;
        private String imagePath;
        private String comment;
        private int likeNum;
        private boolean isShared;
        private Company company;

        public Review toEntity() {
            Review review = Review.builder()
                    .id(id)
                    .yam(yam)
                    .genTime(genTime)
                    .visitTime(visitTime)
                    .imagePath(imagePath)
                    .comment(comment)
                    .likeNum(likeNum)
                    .isShared(isShared)
                    .company(company)
                    .build();
            return review;
        }
    }
}