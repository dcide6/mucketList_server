package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
public class ReviewVO {
    private Long id;
    private LocalDate visitTime;
    private LocalDate genTime;
    private String imagePath;
    private String comment;
    private boolean isShared;
    private Company company;
    private Long likeCount;
    private boolean iLiked;
    private String nickName;
    private String restaurantName;

    public ReviewVO(Review review,
                    String nickName,
                    String restaurantName,
                    Long likeCount,
                    boolean iLiked) {
        this.id = review.getId();
        this.genTime = review.getGenTime();
        this.visitTime = review.getVisitTime();
        this.imagePath = review.getImagePath();
        this.comment = review.getComment();
        this.isShared = review.isShared();
        this.company = review.getCompany();
        this.nickName = nickName;
        this.restaurantName = restaurantName;
        this.likeCount = likeCount != null? likeCount:0;
        this.iLiked = iLiked;
    }

}
