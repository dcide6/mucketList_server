package com.siksaurus.yamstack.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
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
    private String imagePath;
    private String comment;
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private Set<ReviewLike> reviewLikes;
    private boolean isShared;
    private Company company;
    private MealTime mealTime;

    @Builder
    public Review(Long id, Yam yam, LocalDate genTime, LocalDate visitTime, String imagePath,
                  String comment, boolean isShared, Company company, MealTime mealTime){
        this.id = id;
        this.yam = yam;
        this.genTime = genTime == null? LocalDate.now():genTime;
        this.visitTime = visitTime;
        this.imagePath = imagePath;
        this.comment = comment;
        this.isShared = isShared;
        this.company = company;
        this.mealTime = mealTime;
    }
}
