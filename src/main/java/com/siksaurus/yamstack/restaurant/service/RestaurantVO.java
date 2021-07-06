package com.siksaurus.yamstack.restaurant.service;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantVO {
    private Restaurant restaurant;
    private double dist = 0.0;
    private int yamPick = 0;
    private int visitNum = 0;
    private int recommend = 0;
    private int reviewNum = 0;
    private Company company;
    private Set<String> foods;
    private Set<String> tags;
    private List<ReviewVO> reviews;

    public void addCountYamPick() {this.yamPick++;}
    public void addCountVisitNum() {this.visitNum++;}
    public void addCountRecommend() {this.recommend++;}
    public void addCountReviewNum() {this.reviewNum++;}
}
