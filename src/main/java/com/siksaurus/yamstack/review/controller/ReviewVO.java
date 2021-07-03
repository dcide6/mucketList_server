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

    private Account account;
    private Long yamId;

    public ReviewVO(Review review) {
        this.id = review.getId();
        this.genTime = review.getGenTime();
        this.visitTime = review.getVisitTime();
        this.imagePath = review.getImagePath();
        this.comment = review.getComment();
        this.isShared = review.isShared();
        this.company = review.getCompany();
    }
    public void setYamId(Yam yam) {
        this.yamId = yam.getId();
    }
    public void setAccount(Account account) {
        this.account = account;
    }

}
