package com.siksaurus.yamstack.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "yam_id")
    private Yam yam;

    private LocalDate genTime;
    private LocalDate visitTime;

//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ReviewImage> reviewImages = new ArrayList<>();

    private String imagePath;
    private String comment;
    private int likeNum;
    private boolean isShared;
    private Company company;

    @Builder
    public Review(Long id, Yam yam, LocalDate genTime, LocalDate visitTime, String imagePath,
                  String comment, int likeNum, boolean isShared, Company company){
        this.id = id;
        this.yam = yam;
        this.genTime = LocalDate.now();
        this.visitTime = visitTime;
        this.imagePath = imagePath;
        this.comment = comment;
        this.likeNum = likeNum;
        this.isShared = isShared;
        this.company = company;
    }

//    public void addImage(ReviewImage reviewImage) {
//        reviewImages.add(reviewImage);
//        reviewImage.setReview(this);
//    }
//
//    public void removeImage(ReviewImage reviewImage) {
//        reviewImages.remove(reviewImage);
//        reviewImage.setReview(null);
//    }
}
