package com.siksaurus.yamstack.restaurant.service;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantVO {
    private Restaurant restaurant;
    private int yamPick = 0;
    private int visitNum = 0;
    private int recommend = 0;
    private Company company;
    private Set<String> foods;
    private Set<String> tags;

    public void addCountYamPick() {this.yamPick++;}
    public void addCountVisitNum() {this.visitNum++;}
    public void addCountRecommend() {this.recommend++;}
}
