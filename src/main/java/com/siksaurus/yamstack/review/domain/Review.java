package com.siksaurus.yamstack.review.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private long id;

    private LocalDate genTime;
    private LocalDate visitTime;
    private String imagePath;
    private String comment;
    private int likeNum;
    private boolean isShared;
}
