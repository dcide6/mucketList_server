package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.MealTime;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

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
    private MealTime mealTime;
    private Long likeCount;
    private Long reviewCount;
    private boolean iLiked;
    private String nickName;
    private String restaurantName;

    public ReviewVO(Review review,
                    String nickName,
                    String restaurantName,
                    Long likeCount,
                    Long reviewCount,
                    boolean iLiked) {
        this.id = review.getId();
        this.genTime = review.getGenTime();
        this.visitTime = review.getVisitTime();
        this.imagePath = review.getImagePath();
        this.comment = review.getComment();
        this.isShared = review.isShared();
        this.company = review.getCompany();
        this.mealTime = review.getMealTime();
        this.nickName = nickName;
        this.restaurantName = restaurantName;
        this.likeCount = Objects.isNull(likeCount)? 0:likeCount;
        this.reviewCount = Objects.isNull(reviewCount)? 0:reviewCount;
        this.iLiked = iLiked;
    }

}
