package com.siksaurus.yamstack.yam.domain.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.siksaurus.yamstack.yam.controller.YamDTO;
import com.siksaurus.yamstack.yam.domain.Yam;
import javafx.beans.binding.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.siksaurus.yamstack.yam.domain.QYam.yam;
import static com.siksaurus.yamstack.yam.domain.QTag.tag;

@RequiredArgsConstructor
@Repository
public class YamQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Yam> findDynamicQuery(String email, YamDTO.filterYamInfo dto, Pageable pageable) {

        JPAQuery query = this.queryFactory.selectFrom(yam);
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
        }
        if(dto.getTags() != null) {
            query.innerJoin(yam.tags, tag).where(tag.name.in(dto.getTags()));
        }

        //paging
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        QueryResults result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

}
