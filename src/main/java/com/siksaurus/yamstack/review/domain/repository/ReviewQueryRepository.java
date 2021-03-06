package com.siksaurus.yamstack.review.domain.repository;


import com.querydsl.core.Query;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.QReview;
import com.siksaurus.yamstack.review.domain.QReviewLike;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.QYam;
import lombok.RequiredArgsConstructor;
import org.geolatte.geom.crs.Projection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.siksaurus.yamstack.review.domain.QReview.review;
import static com.siksaurus.yamstack.review.domain.QReviewLike.reviewLike;
import static com.siksaurus.yamstack.yam.domain.QYam.yam;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ReviewVO> findReviewsDynamicQuery(Pageable pageable, String email){
        QReview qReview = new QReview("qReview");
        QReviewLike qReviewLike = new QReviewLike("qReviewLike");
        JPAQuery query = this.queryFactory
                .select(Projections.constructor(ReviewVO.class,
                        review,
                        yam.account.name.as("nickName"),
                        yam.restaurant.name.as("restaurantName"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qReviewLike.count())
                                        .from(qReviewLike).where(review.id.eq(qReviewLike.review.id)),"likeCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(qReview.count())
                                        .from(qReview).where(yam.account.name.eq(qReview.yam.account.name)),"reviewCount"

                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(reviewLike)
                                        .from(reviewLike)
                                        .where(reviewLike.account.email.eq(email))
                                        .where(reviewLike.review.eq(review)).exists(),"iLiked"
                        )
                )
                )
                .from(review);

        //searching
        query.where(review.isShared.eq(true));
        query.innerJoin(review.yam, yam).fetchJoin();

        //paging
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        QueryResults result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public ReviewVO findReviewDynamicQuery(Long id, String email) {
        JPAQuery query = this.queryFactory
                .select(Projections.constructor(ReviewVO.class,
                        review,
                        yam.account.name.as("nickName"),
                        yam.restaurant.name.as("restaurantName"),
                        ExpressionUtils.as(
                                JPAExpressions.select(count(reviewLike))
                                        .from(reviewLike).where(reviewLike.review.eq(review)),"likeCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(count(review))
                                        .from(review).where(yam.account.name.eq(review.yam.account.name)),"reviewCount"

                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(reviewLike)
                                        .from(reviewLike)
                                        .where(reviewLike.account.email.eq(email))
                                        .where(reviewLike.review.eq(review)).exists(),"iLiked"
                        )
                        )
                )
                .from(review, yam);

        //searching
        query.where(review.isShared.eq(true));
        query.where(review.id.eq(id));
        query.innerJoin(review.yam, yam);

         ReviewVO result = (ReviewVO) query.fetchFirst();

        return result;
    }
}
