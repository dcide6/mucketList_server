package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
public class ReviewVO {
    private Long id;
    private LocalDate genTime;
    private LocalDate visitTime;
    private String imagePath;
    private String comment;
    private int likeNum;
    private boolean isShared;
    private Company company;

    public ReviewVO(Review review) {
        this.id = review.getId();
        this.genTime = review.getGenTime();
        this.visitTime = review.getVisitTime();
        this.imagePath = review.getImagePath();
        this.comment = review.getComment();
        this.likeNum = review.getLikeNum();
        this.isShared = review.isShared();
        this.company = review.getCompany();
    }
}
