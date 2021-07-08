package com.siksaurus.yamstack.yam.domain.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.siksaurus.yamstack.yam.controller.YamDTO;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.siksaurus.yamstack.account.domain.QAccount.account;
import static com.siksaurus.yamstack.restaurant.domain.QRestaurant.restaurant;
import static com.siksaurus.yamstack.yam.domain.QFood.food;
import static com.siksaurus.yamstack.yam.domain.QYam.yam;
import static com.siksaurus.yamstack.yam.domain.QTag.tag;
import static com.siksaurus.yamstack.review.domain.QReview.review;

@RequiredArgsConstructor
@Repository
public class YamQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Yam> findDynamicQuery(String email, YamDTO.filterYamInfo dto, Pageable pageable) {

        JPAQuery query = this.queryFactory.selectFrom(yam);
        //N+1 회피를 위한 fetch join 적용, food 는 page 기능문제로 hibernate.default_batch_fetch_size 사이즈 변경 적용
        query.leftJoin(yam.account, account).fetchJoin();
        query.leftJoin(yam.restaurant, restaurant).fetchJoin();
        query.leftJoin(yam.review, review).fetchJoin();

        query.where(yam.account.email.eq(email));

        //searching
        if(dto.getRegion() != null) query.where(yam.restaurant.region2depth.eq(dto.getRegion()));
        if(dto.getCategory() != null) query.where(yam.restaurant.category2depth.eq(dto.getCategory()));
        if(dto.getSearchName() != null) query.where(yam.restaurant.name.contains(dto.getSearchName()));
        switch (dto.getMode()) {
            case 1: //얌얌리스트
                query.where(yam.competeTime.isNull());
                break;
            case 2: //완료얌리스트
                query.where(yam.competeTime.isNotNull());
                query.where(yam.isGood.eq(true));
                break;
            case 3: //재방문 의사 없음
                query.where(yam.isGood.eq(false));
                break;
        }
        if(dto.getTags() != null) {
            query.innerJoin(yam.tags, tag).fetchJoin().where(tag.name.in(dto.getTags()));
        }

        //paging
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        QueryResults result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public List<Yam> findBetweenGenTime(LocalDate from, LocalDate to) {
        JPAQuery query = this.queryFactory.selectFrom(yam);
        query.leftJoin(yam.account, account).fetchJoin();
        query.leftJoin(yam.restaurant, restaurant).fetchJoin();
        query.leftJoin(yam.review, review).fetchJoin();

        query.where(yam.genTime.between(from,to));
        QueryResults result = query.fetchResults();
        return result.getResults();
    }

    public List<Yam> findBetweenCompleteTime(LocalDate from, LocalDate to) {
        JPAQuery query = this.queryFactory.selectFrom(yam);
        query.leftJoin(yam.account, account).fetchJoin();
        query.leftJoin(yam.restaurant, restaurant).fetchJoin();
        query.leftJoin(yam.review, review).fetchJoin();

        query.where(yam.competeTime.between(from,to));
        query.where(yam.isGood.eq(true));
        QueryResults result = query.fetchResults();
        return result.getResults();
    }

    public List<Yam> findBetweenCompleteTimeAndNotGood(LocalDate from, LocalDate to) {
        JPAQuery query = this.queryFactory.selectFrom(yam);
        query.leftJoin(yam.account, account).fetchJoin();
        query.leftJoin(yam.restaurant, restaurant).fetchJoin();
        query.leftJoin(yam.review, review).fetchJoin();

        query.where(yam.competeTime.between(from,to));
        QueryResults result = query.fetchResults();
        return result.getResults();
    }
}
