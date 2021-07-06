package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReviewVO {
    private Long id;
    private LocalDate visitTime;
    private LocalDate genTime;
    private String imagePath;
    private String comment;
    private boolean isShared;
    private Company company;
    private int likeCount;

    private String nickName;
    private String restaurantName;

    public ReviewVO(Review review) {
        this.id = review.getId();
        this.genTime = review.getGenTime();
        this.visitTime = review.getVisitTime();
        this.imagePath = review.getImagePath();
        this.comment = review.getComment();
        this.isShared = review.isShared();
        this.company = review.getCompany();
        this.likeCount = review.getReviewLikes() != null ? review.getReviewLikes().size() : 0;
        this.nickName = review.getYam().getAccount().getName();
        this.restaurantName = review.getYam().getRestaurant().getName();
    }

}
