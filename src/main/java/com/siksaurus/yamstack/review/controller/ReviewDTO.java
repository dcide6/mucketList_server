package com.siksaurus.yamstack.review.controller;

import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class ReviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReviewUpdateDTO {

        @NotNull
        private Long id;
        private Yam yam;
        private LocalDate genTime;
        private LocalDate visitTime;
        private String imagePath;
        private String comment;
        private int likeNum;
        private boolean isShared;
        private Company company;

        public ReviewUpdateDTO(Review review){
            this.id = review.getId();
            this.yam = review.getYam();
            this.genTime = review.getGenTime();
            this.visitTime = review.getVisitTime();
            this.imagePath = review.getImagePath();
            this.comment = review.getComment();
            this.likeNum =review.getLikeNum();
            this.isShared = review.isShared();
            this.company = review.getCompany();
        }
    }


}
