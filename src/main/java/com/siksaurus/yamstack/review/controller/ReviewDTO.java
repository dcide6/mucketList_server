package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.MealTime;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
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
        private LocalDate visitTime;
        private String imagePath;
        private String comment;
        private boolean isShared;
        private Company company;
        private MealTime mealTime;

        public Review toEntity() {
            Review review = Review.builder()
                    .yam(yam)
                    .visitTime(visitTime)
                    .imagePath(imagePath)
                    .comment(comment)
                    .isShared(isShared)
                    .company(company)
                    .mealTime(mealTime)
                    .build();
            return review;
        }

        @Builder
        public CreateReviewDTO(Yam yam, LocalDate visitTime, String imagePath,
                               String comment, boolean isShared, Company company, MealTime mealTime){
            this.yam = yam;
            this.visitTime = visitTime;
            this.imagePath = imagePath;
            this.comment = comment;
            this.isShared = isShared;
            this.company = company;
            this.mealTime = mealTime;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateReviewDTO {

        @NotNull
        private Long id;
        private Yam yam;
        private LocalDate visitTime;
        private LocalDate genTime;
        private String imagePath;
        private String comment;
        private boolean isShared;
        private Company company;
        private MealTime mealTime;
        private boolean isImageChanged;

        public Review toEntity() {
            Review review = Review.builder()
                    .id(id)
                    .yam(yam)
                    .visitTime(visitTime)
                    .genTime(genTime)
                    .imagePath(imagePath)
                    .comment(comment)
                    .isShared(isShared)
                    .company(company)
                    .mealTime(mealTime)
                    .build();
            return review;
        }

        @Builder
        public UpdateReviewDTO(Long id, LocalDate visitTime, String imagePath,
                               String comment, boolean isShared, Company company, MealTime mealTime,
                               boolean isImageChanged){
            this.id = id;
            this.visitTime = visitTime;
            this.imagePath = imagePath;
            this.comment = comment;
            this.isShared = isShared;
            this.company = company;
            this.mealTime = mealTime;
            this.isImageChanged = isImageChanged;
        }
    }
}
