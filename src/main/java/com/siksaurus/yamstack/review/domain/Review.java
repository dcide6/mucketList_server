package com.siksaurus.yamstack.review.domain;

import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "yam_id")
    private Yam yam;

    private LocalDate genTime;
    private LocalDate visitTime;
    private String imagePath;
    private String comment;
    private int likeNum;
    private boolean isShared;
    private Company company;

}
